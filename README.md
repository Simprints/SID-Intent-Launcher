# Intents-Launcher
Android app to store and launch intents with params.

# How to use it with SimprintsID

## Enrol
Action: com.simprints.id.REGISTER

Mandatory keys:
- projectId
- userId
- moduleId

## Identify
Action: com.simprints.id.IDENTIFY

Mandatory keys:
- projectId
- userId
- moduleId

## Confirm identification
Action: com.simprints.id.CONFIRM_IDENTITY

Mandatory keys:
- projectId
- userId
- moduleId
- selectedGuid (should be one of the Guids returned from Identify)

## Verify
Action: com.simprints.id.VERIFY

Mandatory Keys:
- projectId
- userId
- moduleId
- verifyGuid (should be Guid that exists in the database)
