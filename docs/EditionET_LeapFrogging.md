## Edition Effective Time LeapFrogging

There exists a subtle implication of dependency effective times when generating deltas for Editions (that is, packages which include the International Edition content).  It's probably best explained with an example:

Let's say that the US release their Edition on 2022-09-01 and this is based on the International Edition 2022-07-31.

When they come to their next release on 2023-03-01, based on the International 2023-01-31 Edition, the delta between these two release will consist of US module content after 2022-09-01, but it needs to ALSO include _International Content_ after 2022-07-31.

We might say that the International Edition effective time leapfrogs over the release date of the country edition.

The Delta Generator Tool needs to look into the MDRS to work out the International Edition date from the previous release, and then select rows based on dates determined on a per module basis.