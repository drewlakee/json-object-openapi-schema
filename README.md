# Json-object to OpenAPI Schema Converting Kotlin Library 

Converts a json object into a OpenAPI schema in YAML format

Supported types:
- arrays
- objects
- primitives

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
    val adapter = JsonOpenApiObjectSchemaAdapter()
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
    // by default prints result in console
    adapter.convert(json)
}
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
