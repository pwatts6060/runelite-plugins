package com.lmsnotifier;

class ColourUtil
{
	static int interpolateBetweenRgbs(int rgb1, int rgb2, double proportion)
	{
		HsvColour first = RgbToHsv(new RgbColour(rgb1));
		HsvColour second = RgbToHsv(new RgbColour(rgb2));

		HsvColour result = new HsvColour(
			first.h + (int) (proportion * (second.h - first.h)),
			first.s + (int) (proportion * (second.s - first.s)),
			first.v + (int) (proportion * (second.v - first.v))
		);

		RgbColour converted = HsvToRgb(result);
		return (converted.r & 0xFF) << 16 | (converted.g & 0xFF) << 8 | converted.b & 0xFF;
	}

	private static RgbColour HsvToRgb(HsvColour hsv)
	{
		RgbColour rgb = new RgbColour(1, 1, 1);
		int region, remainder, p, q, t;

		if (hsv.s == 0)
		{
			rgb.r = hsv.v;
			rgb.g = hsv.v;
			rgb.b = hsv.v;
			return rgb;
		}

		region = hsv.h / 43;
		remainder = (hsv.h - (region * 43)) * 6;

		p = (hsv.v * (255 - hsv.s)) >> 8;
		q = (hsv.v * (255 - ((hsv.s * remainder) >> 8))) >> 8;
		t = (hsv.v * (255 - ((hsv.s * (255 - remainder)) >> 8))) >> 8;

		switch (region)
		{
			case 0:
				rgb.r = hsv.v;
				rgb.g = t;
				rgb.b = p;
				break;
			case 1:
				rgb.r = q;
				rgb.g = hsv.v;
				rgb.b = p;
				break;
			case 2:
				rgb.r = p;
				rgb.g = hsv.v;
				rgb.b = t;
				break;
			case 3:
				rgb.r = p;
				rgb.g = q;
				rgb.b = hsv.v;
				break;
			case 4:
				rgb.r = t;
				rgb.g = p;
				rgb.b = hsv.v;
				break;
			default:
				rgb.r = hsv.v;
				rgb.g = p;
				rgb.b = q;
				break;
		}

		return rgb;
	}

	private static HsvColour RgbToHsv(RgbColour rgb)
	{
		HsvColour hsv = new HsvColour(0, 0, 0);
		int rgbMin, rgbMax;

		rgbMin = rgb.r < rgb.g ? (rgb.r < rgb.b ? rgb.r : rgb.b) : (rgb.g < rgb.b ? rgb.g : rgb.b);
		rgbMax = rgb.r > rgb.g ? (rgb.r > rgb.b ? rgb.r : rgb.b) : (rgb.g > rgb.b ? rgb.g : rgb.b);

		hsv.v = rgbMax;
		if (hsv.v == 0)
		{
			hsv.h = 0;
			hsv.s = 0;
			return hsv;
		}

		hsv.s = 255 * (rgbMax - rgbMin) / hsv.v;
		if (hsv.s == 0)
		{
			hsv.h = 0;
			return hsv;
		}

		if (rgbMax == rgb.r)
		{
			hsv.h = 43 * (rgb.g - rgb.b) / (rgbMax - rgbMin);
		}
		else if (rgbMax == rgb.g)
		{
			hsv.h = 85 + 43 * (rgb.b - rgb.r) / (rgbMax - rgbMin);
		}
		else
		{
			hsv.h = 171 + 43 * (rgb.r - rgb.g) / (rgbMax - rgbMin);
		}

		return hsv;
	}

	static class RgbColour
	{
		int r;
		int g;
		int b;

		RgbColour(int r, int g, int b)
		{
			this.r = r;
			this.g = g;
			this.b = b;
		}

		RgbColour(int rgb)
		{
			this.r = (rgb >> 16) & 0xFF;
			this.g = (rgb >> 8) & 0xFF;
			this.b = rgb & 0xFF;
		}
	}

	static class HsvColour
	{
		int h;
		int s;
		int v;

		HsvColour(int h, int s, int v)
		{
			this.h = h;
			this.s = s;
			this.v = v;
		}
	}
}