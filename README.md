# Vidette
Vidette is a CLI Kotlin app that identifies video files that are inefficiently
encoded and uses ffmpeg to compress them. Currently it only pays attention to
mp4 files, and targets H.264 as its output format.

My motivation was that the MP4 files produced by my phone can frequently be
greatly compressed without perceptible loss of quality - I presume this a
trade-off due to the less powerful hardware of my phone, plus the need to
encode in real time while filming.

## Usage
```
Usage: vidette [<options>] <rootpath>

Options:
  -b, --bitrate-threshold=<float>    Bitrate in MB/s. Re-encoding is attempted for any file above this
                                     threshold
  -c, --minimum-compression=<float>  If re-encoding fails to achieve at least this compression factor the
                                     original file is left unchanged. e.g. 0.5 = re-encoded version must
                                     be no more than half the original size
  --verbose                          More verbose output, including full output from ffmpeg
  -h, --help                         Show this message and exit

Arguments:
  <rootpath>  Path to recursively search from
```

## Install
```
wget https://github.com/MartinEden/vidette/releases/download/1.0.0/vidette-1.0.0.zip
unzip vidette-1.0.0.zip
chmod +x vidette/bin/vidette
```

## Build and run from source
```
./gradlew :installDist
./build/install/vidette/bin/vidette
```

Or simply `gradlew :run`, but then you have to pass arguments in via Gradle.

## Requirements
All of these comes as standard on Ubuntu, which is my only test platform so far.

| Dependency | Use                        |
| ---------- | -------------------------- |
| JVM        |                            |
| ffmpeg     | re-encoding                |
| mediainfo  | identifying file durations |
| gio        | moving files to the trash  |

## Behaviour
If a file is successfully re-encoded, the original file is moved to trash, not
deleted.