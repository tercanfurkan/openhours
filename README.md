# openhours
Spring Reactive API with a functional endpoint to produce a formatted store opening hours

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

**Improvement idea:** For the sake of easier processing of the JSON data for this particular use case the data could be flattened, meaning that the days could have been repeated for each open/close event. Also the keys could be made more readable. Like below:

```
[
   {
      "day":"monday",
      "event":"opening",
      "unixTime":32400
   },
   {
      "day":"monday",
      "event":"closing",
      "unixTime":72000
   },
   {
      "day":"tuesday",
      "event":"opening",
      "unixTime":32400
   },
   ...
]
```
