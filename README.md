# RF2 Delta Generator Tool 2.0

Command line Java tool to calculate the delta from two archives containing SNOMED Full files.

This tool works by scanning through the previous archive and then processing the new archive and outputting any rows that were not present in the previous archive (unless --latest-state is in effect)

### Pre-requisites
* Java 11 Runtime or SDK (or higher)
* The DeltaGeneratorTool.jar file, downloaded from this GitHub project https://github.com/IHTSDO/delta-generator-tool/releases
* Two or more SNOMED CT release archives (.zip) that contains Full files.

### Usage
```
Usage: java -Xms4G -Xmx4G -jar DeltaGeneratorTool <old RF2 Archive Path> <new RF2 Archive Path>  [--latest-state]
```

* RF2 Archive Path - the path to a SNOMED RF2 archive containing some set of full files.

* `-latest-state` this flag extracts only the latest state of each component that has changed . It prevents the delta archive containing 
  multiple states for any single SNOMED CT component (Concept, Description, Relationship or Reference Set Member).

Processing files will be written to a temporary directory, and an archive package will be generated in the current directory.

#### Example (MacOs/Unix):
```
java -Xms4G -Xmx4G -jar DeltaGeneratorTool.jar ~/code/reporting-engine/script-engine/releases/SnomedCT_USEditionRF2_PRODUCTION_20220901T120000Z.zip ~/code/reporting-engine/script-engine/releases/SnomedCT_ManagedServiceUS_PRODUCTION_US1000124_20230301T120000Z.zip
```

#### Example (MS Windows):
```
java -Xms4G -Xmx4G -jar DeltaGeneratorTool.jar %USERPROFILE%\releases\SnomedCT_USEditionRF2_PRODUCTION_20220901T120000Z.zip %USERPROFILE%\releases\SnomedCT_ManagedServiceUS_PRODUCTION_US1000124_20230301T120000Z.zip
```

### Snowstorm Warning
The Snowstorm terminology server is not able to process delta files containing multiple rows for the same component. 

To load a new version of a code system into Snowstorm either use the `--latest-state` flag within this tool when creating a delta archive or simply import a snapshot instead (slower but has the same effect).

## Documentation
- [Frequently Asked Questions FAQ](docs/faq.md)
- [Expected Output](docs/ExpectedOutput.md)
- [Potential Errors](docs/PotentialErrors.md)
- [Validation of Results](docs/Validation.md)
- [Edition Effective Time LeapFrogging](docs/EditionET_LeapFrogging.md)

