## ZipMeUp

Simple Zip Utility using [Zip4J](https://github.com/srikanth-lingala/zip4j)

Supply a json file like this:

```
{
    "includeConfig": true,
    "includeLog": true,
    "log": true,
    "formatOutput": true,
    "input": [
        "C:/Users/Sven/Saved Games/CD Projekt Red/Cyberpunk 2077",
        "C:/Users/Sven/AppData/Local/CD Projekt Red/Cyberpunk 2077",
        "G:/Games/GOG/Cyberpunk 2077/engine/config"
    ],
    "output": [
        "D:/Backups/Cyberpunk/CY2077_Backup_%dd%_%mm%_%yyyy%__%hh%_%mm%_%ss%.zip"
    ]
}
```

And you will get as many Zip Files as you supplied in "output".

Tags:

Key | Meaning
--- | ---
includeConfig | Include the json Config file named "ZipMeUpConfig.json"
includeLog | Include a log of what was done named "ZipMeUpLog.log"
log | Log whats happening in the console
formatOutput | Replace %dd%, %mm% etc with data of time and date
input | List of files/folders to add to the input list
output | List of files/folders to add to the output list

License
----

MIT

Copyright 2020 - 2022 CaptureCoop.org

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.