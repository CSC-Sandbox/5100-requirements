# Display LiDAR Map

## Design

`DisplayLidar` receives LiDAR messages through the provided `Broker`.
`LidarPoint` stores x, y, and z values.
Also, `LidarMapPanel` stores received points and draws their x/y positions in a Swing window.

## Data Format

Each received message uses the following:

```text
LIDAR,x,y,z
```

The program parses all 3 values. The map displays x and y; z is retained (but isn't visualized!)

## Coordinate Mapping

The map uses a fixed range from -5 to 5 on both axes. +x appears to the right meanwhile +y appears upward.

## Run and Test

This is how to run and test.
1. Run `mvn clean test`.
2. Start `TestDisplayLidar`.
3. Start `DisplayLidar`.
4. Observe the map gradually form the simulated environment.

On macOS, I had to turn off AirPlay Receiver because of a message stating port 5000 already being in use.