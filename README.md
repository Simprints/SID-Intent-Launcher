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

### Presets

Any previous intent call can be saved as a preset. To do that:
1. Open "History" tab
1. Select the intent call with required vales
1. Press "Save as preset" and enter the name of the preset and press "Save"

Saved preset will be available in the "Presets" tab for future use. Just press "Use preset" to fill 
the fields with the values from the preset on the intent screen.

### Custom intents

When debugging integration different than LibSimprints custom intents can be used. To do that:
1. On "Intent Launcher" press more icon and select "Custom intents"
1. Input the intent action and extras
   1. Extras must be in the format `key=value` one per line
   1. There is no need to use quotes, commas or any other special characters
1. Press launch
1. Scroll down to see results
   1. The call will also be added to history for future reference, but saving or reusing will not be available

## Building a new APK

There are two ways to release a new APK:

1. Push a new tag that starts with "v" (e.g. "v2023.12.1").
2. Manually trigger the GitHub Action "Deploy to Releases.

In both cases new universal APK will be build and uploaded to the releases section of this repository.
