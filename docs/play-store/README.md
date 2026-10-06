# Google Play listing

The text for the Play Console store listing, one file per field, so it can be reviewed in a pull request and pasted in.
Update the matching file whenever the listing changes.

| File | Play Console field | Limit |
|---|---|---|
| `short-description.txt` | Main store listing, Short description | 80 characters |
| `full-description.txt` | Main store listing, Full description | 4,000 characters |
| `release-notes-en-US.txt` | Production release, What's new (English) | 500 characters |
| `*-es.txt` | The same fields in Spanish (add Spanish under Main store listing, Translations; for the release notes use the Spanish language tag in the "What's new" box) | same limits |

The App name is `moonlight`.

## Keep it true to the app

The listing has to agree with the Data safety form and with the privacy policy
(https://www.jesses.co.tt/privacy-moonlight.html). In particular: approximate location is used on the device only and is
never stored or sent; no accounts, no ads, no advertising ID; usage data and crash reports are off until the user turns
them on. If any of that changes, change all three.

Go through this before pasting a new version:

- Does it describe what is in the release being published, and nothing that is not?
- Are the character limits respected? (Play counts every character, including line breaks.)
- Does it still match the privacy policy and the Data safety answers?

## Not here yet

Graphics (feature graphic, 512 px icon, phone, tablet and Wear screenshots) are in `design/store/android`. They were made
before the gradient was reworked in 0.7, so they need refreshing.
