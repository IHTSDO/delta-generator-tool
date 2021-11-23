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

## Expected output

Here is an example of the output when running the tool. Files are written to a temporary location (in this case /tmp) before they are zipped up in the final output file, which is written to the current directory.  Temporary files are deleted when the process is complete.  If an existing archive file is detected in the current directory, then "_1" (or "_2" etc) is appended to the file name, so previously generated files will not be overwritten.

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ java -jar ./DeltaGeneratorTool.jar 20200731 ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip

SNOMED International RF2 Delta Generator Tool
=============================================

Processing xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Concept_Delta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Description_Delta-en_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_StatedRelationship_Delta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Relationship_Delta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_RelationshipConcreteValues_Delta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Identifier_Delta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_TextDefinition_Delta-en_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_sRefset_OWLExpressionDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_cRefset_AssociationDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_cRefset_AttributeValueDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_Refset_SimpleDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Map/xder2_sRefset_SimpleMapDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Map/xder2_iisssccRefset_ExtendedMapDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Language/xder2_cRefset_LanguageDelta-en_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cciRefset_RefsetDescriptorDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ssRefset_ModuleDependencyDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ciRefset_DescriptionTypeDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_sssssssRefset_MRCMDomainDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cissccRefset_MRCMAttributeDomainDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ssccRefset_MRCMAttributeRangeDelta_INT_20210731.txt
Writing /tmp/DeltaGeneratorTool3375430637151217310/xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cRefset_MRCMModuleScopeDelta_INT_20210731.txt
Latest effective date detected in files: 20210731
Creating archive : xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip from files found in /tmp/DeltaGeneratorTool3375430637151217310/
Created archive: xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
Processing Complete. Rows exported: 558128
Time taken: 23s
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$
```

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
### Validation of results
The resulting zip archive can be examined for correctness as follows.

Firstly, every Full file in the source archive should have a corresponding file in the Delta archive produced.  So in the example given above (and using unix command line tools) I can get a list of the expected files (although the name will change from Full to Delta in the output):

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ unzip -jl ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip "*Full*txt"
Archive:  /home/peterw/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
  Length      Date    Time    Name
---------  ---------- -----   ----
 38629978  2021-05-26 09:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_Concept_Full_INT_20210731.txt
355767418  2021-05-26 09:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_Description_Full-en_INT_20210731.txt
230365824  2021-05-26 09:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_StatedRelationship_Full_INT_20210731.txt
668172523  2021-05-26 09:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_Relationship_Full_INT_20210731.txt
  3194948  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_RelationshipConcreteValues_Full_INT_20210731.txt
       92  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_Identifier_Full_INT_20210731.txt
  3317229  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_TextDefinition_Full-en_INT_20210731.txt
107450051  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Terminology/xsct2_sRefset_OWLExpressionFull_INT_20210731.txt
 22358167  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Content/xder2_cRefset_AssociationFull_INT_20210731.txt
 93730211  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Content/xder2_cRefset_AttributeValueFull_INT_20210731.txt
  1822844  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Content/xder2_Refset_SimpleFull_INT_20210731.txt
 52819888  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Map/xder2_sRefset_SimpleMapFull_INT_20210731.txt
 44836866  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Map/xder2_iisssccRefset_ExtendedMapFull_INT_20210731.txt
410312910  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Language/xder2_cRefset_LanguageFull-en_INT_20210731.txt
    23658  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_cciRefset_RefsetDescriptorFull_INT_20210731.txt
     9329  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_ssRefset_ModuleDependencyFull_INT_20210731.txt
      619  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_ciRefset_DescriptionTypeFull_INT_20210731.txt
   390320  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_sssssssRefset_MRCMDomainFull_INT_20210731.txt
    28379  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_cissccRefset_MRCMAttributeDomainFull_INT_20210731.txt
   104481  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_ssccRefset_MRCMAttributeRangeFull_INT_20210731.txt
      403  2021-05-26 09:31   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Full/Refset/Metadata/xder2_cRefset_MRCMModuleScopeFull_INT_20210731.txt
---------                     -------
2033336138                     21 files
```

So if I then run the same command on the resulting archive, I expect to see an equivalent set of files:

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ unzip -jl xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip "*txt"
Archive:  xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
  Length      Date    Time    Name
---------  ---------- -----   ----
  2470377  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_cRefset_AssociationDelta_INT_20210731.txt
  8402337  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_cRefset_AttributeValueDelta_INT_20210731.txt
    29320  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Content/xder2_Refset_SimpleDelta_INT_20210731.txt
  8284764  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Language/xder2_cRefset_LanguageDelta-en_INT_20210731.txt
  1706205  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Map/xder2_iisssccRefset_ExtendedMapDelta_INT_20210731.txt
   850176  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Map/xder2_sRefset_SimpleMapDelta_INT_20210731.txt
     1209  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cciRefset_RefsetDescriptorDelta_INT_20210731.txt
      101  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ciRefset_DescriptionTypeDelta_INT_20210731.txt
     4225  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cissccRefset_MRCMAttributeDomainDelta_INT_20210731.txt
       82  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_cRefset_MRCMModuleScopeDelta_INT_20210731.txt
    11178  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ssccRefset_MRCMAttributeRangeDelta_INT_20210731.txt
      813  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_ssRefset_ModuleDependencyDelta_INT_20210731.txt
    58008  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Refset/Metadata/xder2_sssssssRefset_MRCMDomainDelta_INT_20210731.txt
  1391422  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Concept_Delta_INT_20210731.txt
  6557571  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Description_Delta-en_INT_20210731.txt
       92  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Identifier_Delta_INT_20210731.txt
  3194948  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_RelationshipConcreteValues_Delta_INT_20210731.txt
 24359872  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_Relationship_Delta_INT_20210731.txt
 20108465  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_sRefset_OWLExpressionDelta_INT_20210731.txt
      114  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_StatedRelationship_Delta_INT_20210731.txt
   359929  2021-10-28 13:30   xSnomedCT_InternationalRF2_BETA_20210731T120000Z/Delta/Terminology/xsct2_TextDefinition_Delta-en_INT_20210731.txt
---------                     -------
 77791208                     21 files
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$
```
Secondly, in terms on the individual files, we can examine the number of rows for each particular effective time, and then for the effective times that we expect to have received in the output, there should be the same number of rows for each effectiveTime.   I'll check the Concept file as an example (using the unix 'cut' command to pull out just the 2nd column, the effectiveTime, in the file and then sorting and asking for a unique count of each value present):

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ unzip -j ~/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip "*Concep*Full*"
Archive:  /home/peterw/Backup/xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
  inflating: xsct2_Concept_Full_INT_20210731.txt
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ cat xsct2_Concept_Full_INT_20210731.txt | cut -f 2 | sort | uniq -c
<ignoring earlier releases...>
   6628 20180131
  21544 20180731
  16311 20190131
  10029 20190731
   6036 20200131
   7564 20200731
   8972 20210131
  13966 20210731
      1 effectiveTime
```
Now, when the tool was run, I asked for rows after 20200731, so I expect to see 8972 + 13966 + 1 x header rows in my delta file produced:

```
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ unzip -j xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip "*Concept*"
Archive:  xSnomedCT_InternationalRF2_BETA_20210731T120000Z.zip
  inflating: xsct2_Concept_Delta_INT_20210731.txt
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$ cat xsct2_Concept_Delta_INT_20210731.txt | cut -f 2 | sort | uniq -c
   8972 20210131
  13966 20210731
      1 effectiveTime
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool/target$
```
I could continue to check all the files present, and then individual rows produced for increased confidence.