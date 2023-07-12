# RF2 Delta Generator Tool

Command line Java tool to extract an RF2 delta from a particular date, from a archive containing SNOMED Full files.

This tool works by scanning through the full files in the package and extracting any row with an effective time  _later_  than the effective time specified.

### Pre-requisites
* Java 11 Runtime or SDK (or higher)
* The DeltaGeneratorTool.jar file, downloaded from this GitHub project https://github.com/IHTSDO/delta-generator-tool/releases
* A SNOMED CT release archive (.zip) that contains Full files.

### Usage
```
Usage: java -Xms1g -jar DeltaGeneratorTool.jar <previousEffectiveTime> <RF2 Archive Path> [<maxEffectiveTime>] [-latest-state]
```

* previousEffectiveTime - the effective time _previously_ ingested ie the effective time of the _last_ release obtained, in format yyyyMMdd.  Any row  _after_  this date will be included in the delta.

* RF2 Archive Path - the path to a SNOMED RF2 archive containing some set of full files.

* maxEffectiveTime - optionally also specify the latest effective time to be included (this date is used inclusively)

* `-latest-state` this flag extracts only the latest state of each component that has changed within the requested date range. It prevents the delta archive containing 
  multiple states for any single SNOMED CT component (Concept, Description, Relationship or Reference Set Member).

Processing files will be written to a temporary directory, and an archive package will be generated in the current directory.   If a maxEffectiveTime has been set, then the effectiveDate in the archive filenames will be replaced with maxEffectiveTime.

#### Example (MacOs/Unix):
```
java -jar DeltaGeneratorTool.jar 20210731 ~/Backup/xSnomedCT_InternationalRF2_MEMBER_20220131T120000Z.zip
```

#### Example (MS Windows):
```
java -jar DeltaGeneratorTool.jar 20210731 %USERPROFILE%\Backup\xSnomedCT_InternationalRF2_MEMBER_20220131T120000Z.zip
```

### Snowstorm Warning
The Snowstorm terminology server is not able to process delta files containing multiple rows for the same component. 

To load a new version of a code system into Snowstorm either use the `-latest-state` flag within this tool when creating a delta archive or simply 
import a snapshot instead (slower but has the same effect).

## Documentation
- [Frequently Asked Questions FAQ](docs/faq.md)
- [Expected Output](docs/ExpectedOutput.md)
- [Potential Errors](docs/PotentialErrors.md)
- [Validation of Results](docs/Validation.md)
- [Edition Effective Time LeapFrogging](docs/EditionET_LeapFrogging.md)

