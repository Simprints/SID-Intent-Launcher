# Intents-Launcher

<img src="./app/src/main/ic_launcher-playstore.png" width="200" height="200">

Android app to store and launch intents with params.

Universal APKs are available in the [releases](https://github.com/Simprints/SID-Intent-Launcher/releases) section of this repository..

## How to use it with SimprintsID

### Biometric flows

1. Open the app (should be on "Intents Launcher" tab)
1. Input mandatory fields on the top of the screen
   - Project ID
   - User ID
   - Module ID
1. Make sure that "BIOMETRIC FLOW INTENT" block is expanded
1. Select the required action
   - Note that for verification "GUID" field is mandatory
1. Scroll down to see results

### Follow up flow

1. Execute "Identify" biometric flow
1. Expand the "FOLLOW UP FLOW INTENT" block
1. Verify that "Session ID" field has correct value from the last request
1. Select required follow up action
   - For confirmation of identity "GUID" field in biometric block is mandatory
1. Scroll down to see results

### Reusing intent data

1. Open "History" tab
1. Select the intent call with required vales
1. Press "Reuse intent fields" to make another intent with the same values

## Building a new APK

There are two ways to release a new APK:

1. Push a new tag that starts with "v" (e.g. "v2023.12.1").
2. Manually trigger the GitHub Action "Deploy to Releases.

In both cases new universal APK will be build and uploaded to the releases section of this repository.
