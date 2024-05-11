# A Kotlin library for converting Json-object into OpenAPI Schema

Converts a json object into an OpenAPI schema in YAML format

Supported types:
- arrays
- objects
- primitives

Features:
- values from json as example

```json
{
  "integer_value": 1,
  "object_value": {
    "a": 2,
    "b": "value"
  }
}
```

```yaml
Schema:
  type: object
  properties:
    integer_value:
      type: integer
    object_value:
      type: object
      properties:
        a:
          type: integer
        b:
          type: string
```
Check specification rendering here: https://editor.swagger.io/

## Usage examples

```kotlin
fun main() {
    var adapter = AdaptersFactory.createObjectAdapter()
    val json = """
            {
              "name": "London",
              "region": "City of London, Greater London",
              "country": "United Kingdom",
              "lat": 51.52,
              "lon": -0.11,
              "tz_id": "Europe/London",
              "localtime_epoch": 1613896955,
              "localtime": "2021-02-21 8:42"
            }
    """
}
```

### Result at need

```kotlin
    val result: String = adapter.convert(json)
    println(result)
```

```yaml
Schema:
  type: object
  properties:
    name:
      type: string
    region:
      type: string
    country:
      type: string
    lat:
      type: number
    lon:
      type: number
    tz_id:
      type: string
    localtime_epoch:
      type: integer
    localtime:
      type: string
```

### Feature toggles for adapter

```kotlin
    adapter = AdaptersFactory.createObjectAdapter(
        Pair(Features.Feature.WITH_EXAMPLE, true)
    )
```

### Custom interface-friendly I/O streams

```kotlin
    adapter.convert(
        // custom suppliers (can be files, API-calls)
        input = JsonTextStream(text = json),
        // prints resulting yaml in console
        output = ConsoleSchemaOutputStream()
    )
```

```yaml
Schema:
  type: object
  properties:
    name:
      type: string
      example: London
    region:
      type: string
      example: "City of London, Greater London"
    country:
      type: string
      example: United Kingdom
    lat:
      type: number
      example: 51.52
    lon:
      type: number
      example: -0.11
    tz_id:
      type: string
      example: Europe/London
    localtime_epoch:
      type: integer
      example: 1613896955
    localtime:
      type: string
      example: 2021-02-21 8:42
```