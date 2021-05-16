# OpenHours Reactive API
Spring Reactive API with a functional endpoint which produces formatted store opening hours based on JSON requests

## What do you need
- JDK11 or later
- Gradle 4+

## How to run
```
git clone https://github.com/tercanfurkan/openhours.git
cd openhours
./gradlew bootRun # to run the REST API
./gradlew test # to run the tests
```

# Thoughts on the POST request JSON body
Current JSON format consist of keys indicating days of a week and corresponding opening hours as values. One JSON file includes data for one restaurant.
```
{
<dayofweek>: <opening hours>
<dayofweek>: <opening hours>
...
}
```
Example JSON data:
```
{
   "monday":[
      {
         "type":"open",
         "value":32400
      },
      {
         "type":"close",
         "value":72000
      }
   ],
   ...
```
**dayofweek :** monday / tuesday / wednesday / thursday / friday / saturday / sunday

**opening hours :** an array of objects containing opening hours. Each object consist of two keys:
- type : open or close
- value : opening / closing time as UNIX time (1.1.1970 as a date), e.g. 32400 = 9 AM , 37800 = 10.30 AM, max value is 86399 = 11.59:59 PM

**Improvement idea:** For the sake of easier processing of the JSON data for this particular use case the data could be flattened, meaning that the days could have been repeated for each open/close event. Also the keys could be made more readable. Finally unixTime can be replaced as minutes past midnight for this specific use case (assuming granularity smaller than minutes is irrelevant. If needed it could be seconds/millis past midnight). Like below:

```
[
   {
      "day":"monday",
      "event":"opening",
      "minutesPastMidnight":0
   },
   {
      "day":"monday",
      "event":"closing",
      "minutesPastMidnight":30
   },
   {
      "day":"tuesday",
      "event":"opening",
      "minutesPastMidnight":60
   },
   ...
]
```
