# RF2 Delta Generator Tool

Command line Java tool to extract an RF2 delta from a particular date, from a archive containing SNOMED Full files.

This tool works by scanning through the full files in the package and extracting any row with an effective time  _later_  than the effective time specified.

```
Usage: java -jar DeltaGeneratorTool <effectiveTime> <RF2 Archive Path> [<maxEffectiveTime>]
```

* effectiveTime - the effective time previously ingested, in format yyyyMMdd.  Any row  _after_  this date will be included in the delta.

* RF2 Archive Path - the path to a SNOMED RF2 archive containing some set of full files.

* maxEffectiveTime - optionally also specify the latest effective time to be included (this date is used inclusively)

Processing files will be written to a temporary directory, and an archive package will be generated in the current directory.   If a maxEffectiveTime has been set, then the effectiveDate in the archive filenames will be replaced with maxEffectiveTime.

