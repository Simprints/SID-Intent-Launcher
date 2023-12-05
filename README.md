# Intents-Launcher

Android app to store and launch intents with params.

Universal APKs are available in the [releases](https://github.com/Simprints/SID-Intent-Launcher/releases) section of this repository..

## How to use it with SimprintsID

### Enrol
Action: com.simprints.id.REGISTER

Mandatory keys:
- projectId
- userId
- moduleId

### Identify
Action: com.simprints.id.IDENTIFY

Mandatory keys:
- projectId
- userId
- moduleId

### Confirm identification
Action: com.simprints.id.CONFIRM_IDENTITY

Mandatory keys:
- projectId
- userId
- moduleId
- selectedGuid (should be one of the Guids returned from Identify)

### Verify
Action: com.simprints.id.VERIFY

Mandatory Keys:
- projectId
- userId
- moduleId
- verifyGuid (should be Guid that exists in the database)

## Building a new APK

There are two ways to release a new APK:

1. Push a new tag that starts with "v" (e.g. "v2023.12.1").
2. Manually trigger the GitHub Action "Deploy to Releases.

In both cases new universal APK will be build and uploaded to the releases section of this repository.
