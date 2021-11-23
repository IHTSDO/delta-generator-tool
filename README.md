# RF2 Delta Generator Tool

Command line Java tool to extract an RF2 delta from a particular date, from a archive containing SNOMED Full files.

This tool works by scanning through the full files in the package and extracting any row with an effective time  _later_  than the effective time specified.

### Pre-requisites
* Java 11 Runtime or SDK (or higher)
* The DeltaGeneratorTool.jar file, downloaded from this GitHub project https://github.com/IHTSDO/delta-generator-tool/releases/new
* A SNOMED CT release archive (.zip) that contains Full files.

### Usage
```
Usage: java -jar DeltaGeneratorTool.jar <previousEffectiveTime> <RF2 Archive Path> [<maxEffectiveTime>]
```

* previousEffectiveTime - the effective time _previously_ ingested ie the effective time of the _last_ release obtained, in format yyyyMMdd.  Any row  _after_  this date will be included in the delta.

* RF2 Archive Path - the path to a SNOMED RF2 archive containing some set of full files.

* maxEffectiveTime - optionally also specify the latest effective time to be included (this date is used inclusively)

Processing files will be written to a temporary directory, and an archive package will be generated in the current directory.   If a maxEffectiveTime has been set, then the effectiveDate in the archive filenames will be replaced with maxEffectiveTime.

## Documentation
- [Frequently Asked Questions FAQ](docs/faq.md)
- [Expected Output](docs/ExpectedOutput.md)
- [Potential Errors](docs/PotentialErrors.md)
- [Validation of Results](Validation.md)

