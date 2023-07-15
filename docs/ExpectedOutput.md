## Expected output

Here is an example of the output when running the tool. Files are written to a temporary location (in this case /tmp) before they are zipped up in the final output file, which is written to the current directory.  Temporary files are deleted when the process is complete.  If an existing archive file is detected in the current directory, then "_1" (or "_2" etc) is appended to the file name, so previously generated files will not be overwritten.

```
peterw@PGW-IHTSDO-3:/mnt/c/Users/peter/code/delta-generator-tool$ java -Xms4G -Xmx4G -jar target/DeltaGeneratorTool.jar ~/code/reporting-engine/script-engine/releases/SnomedCT_USEditionRF2_PRODUCTION_20220901T120000Z.zip ~/code/reporting-engine/script-engine/releases/SnomedCT_ManagedServiceUS_PRODUCTION_US1000124_20230301T120000Z.zip


SNOMED International RF2 Delta Generator Tool mk ii
========================================================

Processing previous archive: SnomedCT_USEditionRF2_PRODUCTION_20220901T120000Z.zip
        Processing sct2_Concept_Full_US1000124_20220901.txt
        Processing sct2_Description_Full-en_US1000124_20220901.txt
        Processing sct2_Identifier_Full_US1000124_20220901.txt
        Processing sct2_Relationship_Full_US1000124_20220901.txt
        Processing sct2_RelationshipConcreteValues_Full_US1000124_20220901.txt
        Processing sct2_StatedRelationship_Full_US1000124_20220901.txt
        Processing sct2_TextDefinition_Full-en_US1000124_20220901.txt
        Processing sct2_sRefset_OWLExpressionFull_US1000124_20220901.txt
        Processing der2_cRefset_AssociationFull_US1000124_20220901.txt
        Processing der2_cRefset_AttributeValueFull_US1000124_20220901.txt
        Processing der2_Refset_SimpleFull_US1000124_20220901.txt
        Processing der2_iisssccRefset_ExtendedMapFull_US1000124_20220901.txt
        Processing der2_sRefset_SimpleMapFull_US1000124_20220901.txt
        Processing der2_cRefset_LanguageFull-en_US1000124_20220901.txt
        Processing der2_cciRefset_RefsetDescriptorFull_US1000124_20220901.txt
        Processing der2_ciRefset_DescriptionTypeFull_US1000124_20220901.txt
        Processing der2_ssRefset_ModuleDependencyFull_US1000124_20220901.txt
        Processing der2_sssssssRefset_MRCMDomainFull_US1000124_20220901.txt
        Processing der2_cissccRefset_MRCMAttributeDomainFull_US1000124_20220901.txt
        Processing der2_ssccRefset_MRCMAttributeRangeFull_US1000124_20220901.txt
        Processing der2_cRefset_MRCMModuleScopeFull_US1000124_20220901.txt
Extracting rows
        Processing sct2_Concept_Full_US1000124_20230301.txt
        Creating sct2_Concept_Delta_US1000124_20230301.txt
        Processing sct2_Description_Full-en_US1000124_20230301.txt
        Creating sct2_Description_Delta-en_US1000124_20230301.txt
        Processing sct2_Identifier_Full_US1000124_20230301.txt
        Creating sct2_Identifier_Delta_US1000124_20230301.txt
        Processing sct2_Relationship_Full_US1000124_20230301.txt
        Creating sct2_Relationship_Delta_US1000124_20230301.txt
        Processing sct2_RelationshipConcreteValues_Full_US1000124_20230301.txt
        Creating sct2_RelationshipConcreteValues_Delta_US1000124_20230301.txt
        Processing sct2_StatedRelationship_Full_US1000124_20230301.txt
        Creating sct2_StatedRelationship_Delta_US1000124_20230301.txt
        Processing sct2_TextDefinition_Full-en_US1000124_20230301.txt
        Creating sct2_TextDefinition_Delta-en_US1000124_20230301.txt
        Processing sct2_sRefset_OWLExpressionFull_US1000124_20230301.txt
        Creating sct2_sRefset_OWLExpressionDelta_US1000124_20230301.txt
        Processing der2_cRefset_AssociationFull_US1000124_20230301.txt
        Creating der2_cRefset_AssociationDelta_US1000124_20230301.txt
        Processing der2_cRefset_AttributeValueFull_US1000124_20230301.txt
        Creating der2_cRefset_AttributeValueDelta_US1000124_20230301.txt
        Processing der2_Refset_SimpleFull_US1000124_20230301.txt
        Creating der2_Refset_SimpleDelta_US1000124_20230301.txt
        Processing der2_iisssccRefset_ExtendedMapFull_US1000124_20230301.txt
        Creating der2_iisssccRefset_ExtendedMapDelta_US1000124_20230301.txt
        Processing der2_sRefset_SimpleMapFull_US1000124_20230301.txt
        Creating der2_sRefset_SimpleMapDelta_US1000124_20230301.txt
        Processing der2_cRefset_LanguageFull-en_US1000124_20230301.txt
        Creating der2_cRefset_LanguageDelta-en_US1000124_20230301.txt
        Processing der2_cciRefset_RefsetDescriptorFull_US1000124_20230301.txt
        Creating der2_cciRefset_RefsetDescriptorDelta_US1000124_20230301.txt
        Processing der2_ciRefset_DescriptionTypeFull_US1000124_20230301.txt
        Creating der2_ciRefset_DescriptionTypeDelta_US1000124_20230301.txt
        Processing der2_ssRefset_ModuleDependencyFull_US1000124_20230301.txt
        Creating der2_ssRefset_ModuleDependencyDelta_US1000124_20230301.txt
        Processing der2_sssssssRefset_MRCMDomainFull_US1000124_20230301.txt
        Creating der2_sssssssRefset_MRCMDomainDelta_US1000124_20230301.txt
        Processing der2_cissccRefset_MRCMAttributeDomainFull_US1000124_20230301.txt
        Creating der2_cissccRefset_MRCMAttributeDomainDelta_US1000124_20230301.txt
        Processing der2_ssccRefset_MRCMAttributeRangeFull_US1000124_20230301.txt
        Creating der2_ssccRefset_MRCMAttributeRangeDelta_US1000124_20230301.txt
        Processing der2_cRefset_MRCMModuleScopeFull_US1000124_20230301.txt
        Creating der2_cRefset_MRCMModuleScopeDelta_US1000124_20230301.txt
        Creating archive from /tmp/DeltaGeneratorTool9197009719783813653
Creating archive: SnomedCT_ManagedServiceUS_PRODUCTION_US1000124_20230301T120000Z_2.zip
Processing Complete. Rows exported: 163776
Time taken: 466s
peterw@PGW-IHTSDO-3:~/code/delta-generator-tool$
```
