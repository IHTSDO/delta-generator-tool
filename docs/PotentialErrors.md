### Potential errors

* Failing to say .jar in the name of the tool, will result in "Error: Unable to access jarfile DeltaGeneratorTool"

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ java -jar DeltaGeneratorTool 20200731 ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
Error: Unable to access jarfile DeltaGeneratorTool
```

* Specifying the parameters in the wrong order will result in the following error.  Command line parameters must be supplied in the order specified.  A similar error will be received for effectiveTimes not exactly matching the pattern YYYYMMDD, so 2020-07-31 would be considered invalid.

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ java -jar DeltaGeneratorTool.jar ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip 20200731
SNOMED International RF2 Delta Generator Tool
=============================================

Exception in thread "main" java.lang.IllegalArgumentException: Invalid previous effective time (yyyyMMdd): /home/peterw/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
        at org.snomed.otf.delta.DeltaGeneratorTool.main(DeltaGeneratorTool.java:41)
```

*  If the archive file cannot be found, the following error will be generated:

```
peterw@PGW-IHTSDO-3: ~/code/delta-generator-tool/target$ java -jar DeltaGeneratorTool.jar 20200231 ~/Backup/xSnomedCT_InternationalRF2_BETA_18210731T120000Z.zip
SNOMED International RF2 Delta Generator Tool
=============================================

Exception in thread "main" java.io.IOException: '/home/peterw/Backup/xSnomedCT_InternationalRF2_BETA_18210731T120000Z.zip' could not be read as an archive file.
        at org.snomed.otf.delta.DeltaGeneratorTool.validateFile(DeltaGeneratorTool.java:119)
        at org.snomed.otf.delta.DeltaGeneratorTool.main(DeltaGeneratorTool.java:43)
```