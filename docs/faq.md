### Frequently Asked Questions

##### Why is this tool required now?
SNOMED International have determined that most installations of SNOMED CT perform updates using the Snapshot set of RF2 files.  The Snapshot files represent the complete current state of the product. A few installations may use Delta files as they are more space efficient during transfer.  However, as SNOMED International moves to producing the International Edition of SNOMED CT more regularly, it becomes more likely that a delta could be missed and - when working with deltas - it is _essential_ that every delta is processed or the hierarchy formed will be incorrect.   For this reason, SNOMED International suggests that Snapshot files are consumed. This tool is being made available for anyone who wishes to continue to work with Delta releases.

##### Who should be running this utility?
Any installation which is not able to consume the Snapshot release of SNOMED CT should use this tool to produce the relevant Delta package.  Care 

##### What is the expected outcome of running the utility?
The tool will take in an archive containing Full file and filter these based on the effective date(s) given.   So any row that contains a date after the previousEffectiveTime will be included, and optionally, any row up to and including the maxEffectiveTime.

The files produced will have the same names as in the input files, only with the 'Full' part of the filename changed to 'Delta'.   The set of files (which are written to a temporary directory) are then zipped up and the archive written to the current working directory.

The expected output written to the command line (STDOUT) is shown in [Expected Output](docs/ExpectedOutput.md)

##### How do I validate the results of the Delta Generation Tool? 
See discussion here: [Validation](docs/Validation.md)

##### Is there a service/front end to run this from as yet?
At this time, no web or graphical user interface exists for this command line tool.

##### Who should I contact if I need support/want to suggest an improvement?
In the first instance, please raise an issue against this project in GitHub ie [https://github.com/IHTSDO/delta-generator-tool/issues](https://github.com/IHTSDO/delta-generator-tool/issues)