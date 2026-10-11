#!/usr/bin/env python3
"""Captioned phone screenshots and a lunar-month timelapse video for the Play store listing.

    python3 tools/store_graphics/make_store_graphics.py              # both
    python3 tools/store_graphics/make_store_graphics.py screenshots  # or just one
    python3 tools/store_graphics/make_store_graphics.py video

Needs Pillow, numpy and ffmpeg (with libx264), and the Inter font (OFL; pass another with --font). The screenshots are
made from the plain captures in docs/screenshots/phone; the video's colours come from a port of
GradientUtil.moonHsl, so keep the two in step if the colour mapping changes.
"""

import argparse
import colorsys
import math
import os
import shutil
import subprocess
import tempfile

import numpy as np
from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
SHOTS = os.path.join(ROOT, "docs", "screenshots")
FONT_DIR = "/usr/share/fonts/opentype/inter"

TEXT = (0x22, 0x22, 0x22)  # GradientUtil.TextColor
SILVER = (0xC0, 0xC0, 0xC0)
LIGHT_BLUE = (0xCC, 0xE5, 0xFF)
WINDOW = (0x30, 0x30, 0x30)

CAPTIONS = {
    "en": {
        "01-full-moon": "Your screen follows the moon",
        "02-data": "The numbers behind the colour",
        "03-crescent": "Blue at new moon, gold at full",
        "04-about": "No ads, no accounts, private by default",
    },
    "es": {
        "01-full-moon": "Tu pantalla sigue a la luna",
        "02-data": "Los números detrás del color",
        "03-crescent": "Azul en luna nueva, dorado en luna llena",
        "04-about": "Sin anuncios, sin cuentas, privada",
    },
}


def font(name, size, override=None):
    return ImageFont.truetype(override or os.path.join(FONT_DIR, name), size)


def wrap(draw, text, fnt, width):
    """One line if it fits, else two lines as even as possible (no orphaned last word)."""
    if draw.textlength(text, font=fnt) <= width:
        return [text]
    words = text.split()
    splits = [(" ".join(words[:i]), " ".join(words[i:])) for i in range(1, len(words))]
    return list(min(splits, key=lambda p: max(draw.textlength(line, font=fnt) for line in p)))


# --- Screenshots ---------------------------------------------------------------------------------------------------

def captioned(shot, caption, font_path=None):
    """The capture scaled down with rounded corners and a shadow, under a caption, on the capture's own gradient."""
    w, h = shot.size
    # The background is the app's gradient: one column from the edge of the capture, stretched across.
    canvas = shot.crop((4, 0, 5, h)).resize((w, h)).convert("RGB")
    draw = ImageDraw.Draw(canvas)

    fnt = font("Inter-SemiBold.otf", 84, font_path)
    lines = wrap(draw, caption, fnt, w - 160)
    line_h = 104
    top = 150 - (len(lines) - 1) * line_h // 2
    for i, line in enumerate(lines):
        draw.text((w / 2, top + i * line_h), line, font=fnt, fill=TEXT, anchor="mm")

    scale = 0.74
    sw, sh = int(w * scale), int(h * scale)
    small = shot.convert("RGB").resize((sw, sh), Image.LANCZOS)
    x, y = (w - sw) // 2, h - sh - 90
    radius = 44
    mask = Image.new("L", (sw, sh), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, sw - 1, sh - 1), radius, fill=255)

    shadow = Image.new("L", (w, h), 0)
    ImageDraw.Draw(shadow).rounded_rectangle((x, y + 16, x + sw, y + sh + 16), radius, fill=110)
    shadow = shadow.filter(ImageFilter.GaussianBlur(28))
    canvas = Image.composite(Image.new("RGB", (w, h), (0, 0, 0)), canvas, shadow)
    canvas.paste(small, (x, y), mask)
    ImageDraw.Draw(canvas).rounded_rectangle((x, y, x + sw - 1, y + sh - 1), radius, outline=(255, 255, 255),
                                             width=3)
    return canvas


def make_screenshots(font_path=None):
    for lang, captions in CAPTIONS.items():
        out_dir = os.path.join(SHOTS, "phone-captioned", lang)
        os.makedirs(out_dir, exist_ok=True)
        for name, caption in captions.items():
            shot = Image.open(os.path.join(SHOTS, "phone", f"{name}.png"))
            out = os.path.join(out_dir, f"{name}.png")
            captioned(shot, caption, font_path).save(out, optimize=True)
            print(out)


# --- Timelapse video -----------------------------------------------------------------------------------------------

def moon_hsl(phase, fraction, altitude=45.0):
    """Port of GradientUtil.moonHsl: (hue degrees, saturation, lightness, alpha)."""
    illumination = min(max(fraction, 0.0), 1.0)
    up = min(max(altitude / 90.0, 0.0), 1.0)
    night = min(max(-altitude / 12.0, 0.0), 1.0)
    tilt = 25.0 * math.sin(math.radians(phase))
    hue = (230.0 + (45.0 - 230.0) * illumination + tilt) % 360.0
    saturation = (0.25 + (0.65 - 0.25) * up) * (1.0 - 0.6 * night)
    lightness = (0.35 + (0.75 - 0.35) * illumination) * (1.0 - 0.5 * night)
    alpha = 0.25 + 0.75 * illumination
    return hue, saturation, lightness, alpha


def gradient_column(height, hsl):
    """The screen top to bottom, as GradientUtil draws it at 270 degrees over the dark window (rows x RGB)."""
    hue, s, l, a = hsl
    r, g, b = colorsys.hls_to_rgb(hue / 360.0, l, s)
    moon = np.array([r * 255, g * 255, b * 255, a])
    silver = np.array([*SILVER, 1.0])
    blue = np.array([*LIGHT_BLUE, 1.0])
    t = 1.0 - (np.arange(height) + 0.5) / height  # 0 at the bottom, 1 at the top
    lower = t < 0.5
    u = np.where(lower, t / 0.5, (t - 0.5) / 0.5)[:, None]
    start = np.where(lower[:, None], moon, silver)
    end = np.where(lower[:, None], silver, blue)
    c = start + (end - start) * u
    rgb = c[:, :3] * c[:, 3:4] + np.array(WINDOW) * (1 - c[:, 3:4])
    return np.clip(rgb, 0, 255).astype(np.uint8)


def phase_label(phase, fraction):
    percent = round(fraction * 100)
    if percent >= 99:
        return "Full moon"
    if percent <= 1:
        return "New moon"
    return f"{'Waxing' if phase < 0 else 'Waning'} moon, {percent}% lit"


def make_video(font_path=None, seconds=20, fps=30, size=(1080, 1920)):
    if not shutil.which("ffmpeg"):
        raise SystemExit("ffmpeg is not installed")
    w, h = size
    title_font = font("Inter-Light.otf", 120, font_path)
    label_font = font("Inter-Regular.otf", 52, font_path)
    end_font = font("Inter-SemiBold.otf", 64, font_path)
    frames = seconds * fps
    hold = 3 * fps  # the last three seconds hold the full moon with the store line
    out = os.path.join(SHOTS, "video", "moonlight-lunar-month.mp4")
    os.makedirs(os.path.dirname(out), exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        for i in range(frames):
            # One lunar month, new to full to new, then hold on the full moon for the ending.
            ending = i >= frames - hold
            phase = 0.0 if ending else -180.0 + 360.0 * i / (frames - hold - 1)
            fraction = (1 + math.cos(math.radians(phase))) / 2
            column = gradient_column(h, moon_hsl(phase, fraction))
            frame = Image.fromarray(np.repeat(column[:, None, :], w, axis=1), "RGB")
            draw = ImageDraw.Draw(frame)
            draw.text((w / 2, 300), "moonlight", font=title_font, fill=TEXT, anchor="mm")
            draw.text((w / 2, 420), phase_label(phase, fraction), font=label_font, fill=TEXT, anchor="mm")
            if ending:
                draw.text((w / 2, 640), "Free on Google Play", font=end_font, fill=TEXT, anchor="mm")
                draw.text((w / 2, 730), "Phone, wallpaper, widget, Wear OS", font=label_font, fill=TEXT,
                          anchor="mm")
            frame.save(os.path.join(tmp, f"{i:04d}.png"))
        subprocess.run(["ffmpeg", "-y", "-loglevel", "error", "-framerate", str(fps), "-i",
                        os.path.join(tmp, "%04d.png"), "-c:v", "libx264", "-pix_fmt", "yuv420p", "-crf", "20",
                        "-movflags", "+faststart", out], check=True)
    print(out)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("what", nargs="?", choices=["screenshots", "video", "all"], default="all")
    p.add_argument("--font", help="A font file to use instead of Inter")
    args = p.parse_args()
    if args.what in ("screenshots", "all"):
        make_screenshots(args.font)
    if args.what in ("video", "all"):
        make_video(args.font)


if __name__ == "__main__":
    main()
