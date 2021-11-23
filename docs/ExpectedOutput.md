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
