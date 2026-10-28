# Google Play listing

The text for the Play Console store listing, one file per field, so it can be reviewed in a pull request and pasted in.
Update the matching file whenever the listing changes.

| File | Play Console field | Limit |
|---|---|---|
| `title.txt` | Main store listing, App name | 30 characters |
| `short-description.txt` | Main store listing, Short description | 80 characters |
| `full-description.txt` | Main store listing, Full description | 4,000 characters |
| `release-notes-en-US.txt` | Production release, What's new (English) | 500 characters |
| `*-es.txt` | The same fields in Spanish (add Spanish under Main store listing, Translations; for the release notes use the Spanish language tag in the "What's new" box) | same limits |

The app name in the store carries the words people search for (moon phase, wallpaper); the brand stays lowercase
`moonlight` in the app and on the icon. Play counts the title for search more than any other field, then the short
description.

## While 0.6 is in production

The title and short description name the live wallpaper, which only exists from 0.7. Until 0.7 reaches production,
use these instead:

| Field | English | Spanish |
|---|---|---|
| App name | Moonlight: Moon Phase Glow | Moonlight: fases de la luna |
| Short description | A calm glow that follows tonight's moon phase, on your phone and Wear OS watch. | Un brillo sereno que sigue la fase lunar, en tu teléfono y tu reloj Wear OS. |

Change one field at a time and add a line to the experiments log (`/mnt/project-files/monitoring/experiments.md`), so
the Monday growth report can show whether the store listing conversion moved.

## Keep it true to the app

The listing has to agree with the Data safety form and with the privacy policy
(https://www.jesses.co.tt/privacy-moonlight.html). In particular: approximate location is used on the device only and is
never stored or sent; no accounts, no ads, no advertising ID; usage data and crash reports are off until the user turns
them on. If any of that changes, change all three.

Go through this before pasting a new version:

- Does it describe what is in the release being published, and nothing that is not?
- Are the character limits respected? (Play counts every character, including line breaks.)
- Does it still match the privacy policy and the Data safety answers?

## Graphics

The screenshots, 512 px icon and feature graphic are in `../screenshots`.
