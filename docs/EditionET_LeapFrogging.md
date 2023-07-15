## Edition Effective Time LeapFrogging

Dependency effective times have subtle implications when generating deltas for Editions (that is, packages which include the International Edition content).  It's probably best explained with an example:

Let's say that the US release their Edition on 2022-09-01, based on the International Edition 2022-07-31.

When they come to their _next_ release on 2023-03-01, based on the International 2023-01-31 Edition, the delta between these two release will consist of US module content after 2022-09-01, but it needs to ALSO include _International Content_ after 2022-07-31.

We might say that the International Edition effective time leapfrogs over the release date of the country edition.

Version 2.0 of the Delta Generator Tool avoids parsing the Module Dependency ReferenceSet to calculate the appropriate effective times to include for each module, by considering the previous Full archive and simply including any rows from the new archive which are not present in the previous one.  This approach means that International Content that is _earlier_ than the previous Edition effective time will be included where necessary.