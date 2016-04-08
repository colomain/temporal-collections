The Temporal Collections library is an implementation of several "temporal" data patterns, along with Hibernate custom collection wrappers for defining temporal entity relationships. Many of the concepts used in developing this library were borrowed from Martin Fowler's blog on [patterns for things that change with time](http://martinfowler.com/ap2/timeNarrative.html).

## Temporal Data ##

Temporal data is, quite simply put, data that changes with time. A temporal data value is said to be "effective" during a certain period of time. Thus there is a start and end to each data point. In many cases, our applications are only concerned with the current value, and older historical values are archived in an audit log or something of that nature. However, there are many use cases where we frequently need access to data at other points in time. In addition, there may be a need to modify the history. Keeping the dates and values in the historical time line consistent can be very cumbersome to do manually. It is for these reasons that this library was created.

Temporal data has two required properties: a start date and an end date. Domain models make use of the Temporal Collections API by extending `org.kowboy.temporal.TemporalData` or one of its subclasses (usually `org.kowboy.temporal.NormalizedTemporalData`). Subclasses will inherit methods for getting and setting the TimePeriod, a component wrapping the start and end dates.

_Yes, this is intrusive for your object model, but it is the simplest approach. You may decide it suits your application better to create your own custom collections for your model. However, if you have more than a few temporal relationships, you will find yourself repeating a lot of code._

## Time Lines ##

As a data value (or set of values) changes with time, you end up with a history (a collection) of values. We have chosen to call this a "time line". You can think of a time line as an array that is indexed by effective date, rather than position within the collection. However, whereas an array element has only one index, a time line element can be indexed using any point in time within the range of its time period. Consider the following snippet of the `org.kowboy.temporal.TimeLine` interface:

```
public interface TimeLine extends Set {

    /**
     * Gets the object from this collection that was effective
     * as of the Date specified.
     * @param asOf Date for which TimePeriod is returned
     * @return TimePeriod
     */
    TemporalData getAsOf(Date asOf);
    
    ...
}
```

Notice that the interface extends `java.util.Set`. This is because some temporal data have set-like semantics in that, there can be only one data value for a given effective date (no overlaps). The TimeLine implementations help maintain consistency by automatically adjusting start and end dates when data values (and their time periods) are inserted into the time line.

Later, in the Denormalized Time Lines section, we will see a slightly modified version of this set-like assumption.

### Period of Existence ###

The first type of TimeLine is the most general. A "period of existence" time line has the base restriction of not allowing time period overlaps. However, it does allow adjacent periods and gaps. Consider the following table:

| Phone Number   | Start Date       | End Date        |
|:---------------|:-----------------|:----------------|
| 123-4567       | 2006-09-10       | 2007-04-13      |
| 234-5678       | 2007-05-20       | 9999-12-31      |

Notice that there is a gap between April 14 and May 19 where there is no value for the Phone Number. If you were to call `getAsOf` with a date of 2007-04-20, the result would be `null`. This gap is typical of a period of existence. Use this type whenever the nature of the data allows there to be periods with no value.

Another thing you may have noticed is the end date of 9999-12-31. I call this "End of Time", or EOT for short. It basically means that this record does not have an end date defined yet. I could specify an end date for it, at which point I would refer to it as a "terminated" record. The first phone number was terminated as of 2007-04-13.

### Perpetual Time Line ###

The second type of TimeLine is more restrictive. A "perpetual" time line requires that, once a value has been put in the time line, there must be no gaps until the end of time (EOT). The purpose of this is to model data that, once it exists, must **always** have a value. Another way to put it is that each record's end date must extend to the day before the next record's start date, and the last record must have an end date of EOT. The following time line satisfies this definition:

| Phone Number   | Start Date       | End Date        |
|:---------------|:-----------------|:----------------|
| 123-4567       | 2006-09-10       | 2007-04-13      |
| 555-4567       | 2007-04-14       | 2007-05-19      |
| 234-5678       | 2007-05-20       | 9999-12-31      |

The only difference between this and the previous table is that there are no gaps. If we removed the second phone record, the end date of the first record would be extended like so:

| Phone Number   | Start Date       | End Date        |
|:---------------|:-----------------|:----------------|
| 123-4567       | 2006-09-10       | **2007-05-19**    |
| 234-5678       | 2007-05-20       | 9999-12-31      |

Why do we extend the previous record? We are making the assumption that a record is effective until the next record in time. Therefore we must extend the previous record forward (as opposed to changing the start date of the subsequent record).

## Denormalized Time Lines ##

It is often the case that multiple, parallel time lines must co-exist in the same relationship. This may be a natural way to express the relationship, or it could be forced upon you by the relational model (database schema). Entities which must exist in a denormalized relationship should extend TemporalData and implement `getTimeLineKey()` and `setTimeLineKey` for accessing the property that differentiates the parallel time lines. Let's expand our previous example to make this clear:

| Phone Number   | Phone Type | Start Date  | End Date    |
|:---------------|:-----------|:------------|:------------|
| 123-4456       | W          | 2006-09-10  | 2007-04-13  |
| 321-3422       | W          | 2007-04-14  | 9999-12-31  |
| 893-2235       | H          | 2006-09-10  | 2008-02-13  |
| 123-4456       | H          | 2008-02-14  | 9999-12-31  |

Although this example is a bit contrived, it illustrates the point. Despite the overlap in time periods, all of these records can exist in the same TimeLine collection as long as `getTimeLineKey()` returns the Phone Type. Actually, a normalized time line is merely a simplified case of a denormalized time line where the time line key is constant. In fact, this is how `NormalizedTemporalData` is implemented (see the source).

## Hibernate Custom User Types ##

Everything in package `org.kowboy.temporal` is completely usable as a stand-alone API, and can of course be used outside of Hibernate and perhaps with other ORM implementations (although, some design decisions were made with Hibernate in mind). This section will explain how to model your objects and how to write the Hibernate mappings for persisting them.

### The Object Model ###

Here is an example of a Person object with a temporal relationship to PhoneNumber.

```
public class Person {
	private Integer id;
	private String firstName;
	private String lastName;
	private TimeLine phoneHistory = new PeriodOfExistenceTimeLine();

	...snip...
	
	public void addPhoneHistory(PhoneNumber ph) {
		phoneHistory.add(ph);
	}
	
	public String getNumberString(Date asOf) {
		return (String) phoneHistory.getProperty("numberString", asOf);
	}
	
	public void setNumberString(String numberString, TimePeriod period) {
		phoneHistory.setProperty("numberString", period, numberString, new PhoneNumberFactory());
	}
	
	public Integer getAreaCode(Date asOf) {
		return (Integer) phoneHistory.getProperty("areaCode", asOf);
	}
	
	public void setAreaCode(Integer areaCode, TimePeriod period) {
		phoneHistory.setProperty("areaCode", period, areaCode, new PhoneNumberFactory());
	}
	
	class PhoneNumberFactory implements TemporalDataFactory {
		public TemporalData newInstance() {
			return new PhoneNumber();
		}
	}
}
```

It is not necessary to create these convenience methods such as `getNumberString(Date asOf)`. We could simply get the phone number through `person.getPhoneHistory().getAsOf(date).getNumberString()`, but you can see that the convenience method looks much nicer and abstracts away the collection, making it feel as though the data is part of the Person object (and not buried in another object inside a collection).

And here is the interesting bits of the PhoneNumber class:

```
public class PhoneNumber extends NormalizedTemporalData {
	private Integer id;
	private String numberString;
	private Integer areaCode;
	
	public Object cloneData() {
		PhoneNumber pn = new PhoneNumber();
		pn.setNumberString(numberString);
		pn.setAreaCode(areaCode);
		return pn;
	}

	public boolean equalsIgnorePeriod(TemporalData d) {
		if (d == null) return false;
		if (!d.getClass().equals(PhoneNumber.class)) return false;
		PhoneNumber pn = (PhoneNumber) d;
		if (!Utils.nullSafeEquals(numberString, pn.numberString) || 
				!Utils.nullSafeEquals(areaCode, pn.areaCode)) return false;		
		return true;
	}

	public Object getIdentity() {
		return id;
	}

	public void setIdentity(Object identity) {
		this.id = (Integer) identity;
	}
}
```

There are several things here worth mentioning. First, the `cloneData` and `equalsIgnorePeriod` are used by the TimeLine implementations to split and merge adjacent time periods. The merging of adjacent "equal" records is done to optimize and avoid fragmentation. The `identity` getter and setter are used for the purpose of reusing database IDs when objects are being removed and added into the TimeLine. You can return `null` for this, but you will be potentially wasting a lot of database IDs. Consider your transaction volume and code accordingly.

### The Mappings ###

The mapping for Person will include a one-to-many relationship to PhoneNumber. You should map this as a bag, since our custom type has its own semantic behavior which is not quite the same as a set. In the bag element, you will define the `collection-type` attribute to be one of the following:

  * org.hibernate.usertype.PeriodOfExistenceType
  * org.hibernate.usertype.PerpetualType
  * org.hibernate.usertype.DenormalizedPeriodOfExistenceType
  * org.hibernate.usertype.DenormalizedPerpetualType

Here is the XML configuration for the Person -> PhoneNumber example:

```
<set name="phoneHistory" lazy="false" cascade="all-delete-orphan"
	collection-type="org.hibernate.usertype.PeriodOfExistenceType" order-by="ID">
	<key column="PERSON_ID"/>
	<one-to-many class="org.kowboy.temporal.domain.PhoneNumber"/>
</set>
```

Note: I chose not to map this as a bidirectional relationship. However, doing so would yield some performance benefits. Consider mapping this with `inverse="true"` on the `set` element, and adding a many-to-one mapping on `PhoneNumber`.


---


That's really all there is to it. For more usage examples, see the unit tests.

## Future Enhancements ##

There are several features and improvements I would like to implement in the near-term:

  * Proper use of generics to make the TimeLine collections type-safe.
  * Change the precision of TimePeriods to be seconds or even milliseconds (it is currently 24 hours). Thus, one record would end at 23:59:59 and the next record would start at 24:00:00. Currently the increment is 1 day. Some apps will need finer precision.