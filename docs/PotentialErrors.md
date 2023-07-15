### Potential errors

* Failing to say .jar in the name of the tool, will result in "Error: Unable to access jarfile DeltaGeneratorTool"

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ java -jar DeltaGeneratorTool 20200731 ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
Error: Unable to access jarfile DeltaGeneratorTool
```


*  If the archive file cannot be found, the following error will be generated:

```
peterw@PGW-IHTSDO-3: ~/code/delta-generator-tool/target$ java -jar DeltaGeneratorTool.jar  ~/Backup/SnomedCT_InternationalRF2_20220731T120000Z.zip ~/Backup/xSnomedCT_InternationalRF2_BETA_20230131T120000Z.zip
SNOMED International RF2 Delta Generator Tool
=============================================

Exception in thread "main" java.io.IOException: '/home/peterw/Backup/xSnomedCT_InternationalRF2_BETA_20230131T120000Z.zip' could not be read as an archive file.
        at org.snomed.otf.delta.DeltaGeneratorTool.validateFile(DeltaGeneratorTool.java:119)
        at org.snomed.otf.delta.DeltaGeneratorTool.main(DeltaGeneratorTool.java:43)
```