package com.bdb.bikedeboa.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.bdb.bikedeboa.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Hashtable;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.R.attr.rating;

public final class AssetHelper {

	private static Resources resources;
	private static Map<String, Drawable> structureTypeImageMap;
	private static Map<String, String> structureTypeStringMap;
	private static Map<String, Drawable> ownershipImageMap;
	private static Map<String, String> ownershipStringMap;

	public static void init(Resources resources) {
		AssetHelper.resources = resources;
		// Initialize hashtables of resources
		initMaps();
		// Load fonts
		CalligraphyConfig.initDefault(
				new CalligraphyConfig.Builder()
						.setDefaultFontPath("Raleway-Regular.ttf")
						.setFontAttrId(R.attr.fontPath)
						.build());
	}

	private static void initMaps() {

		structureTypeImageMap = new Hashtable<>();
		structureTypeImageMap.put("uinvertido",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_uinvertido, null));
		structureTypeImageMap.put("deroda",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_deroda, null));
		structureTypeImageMap.put("trave",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_trave, null));
		structureTypeImageMap.put("suspenso",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_suspenso, null));
		structureTypeImageMap.put("grade",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_grade, null));
		structureTypeImageMap.put("other",
				ResourcesCompat.getDrawable(resources, R.drawable.tipo_other, null));

		structureTypeStringMap = new Hashtable<>();
		structureTypeStringMap.put("uinvertido", resources.getString(R.string.uinvertido));
		structureTypeStringMap.put("deroda", resources.getString(R.string.deroda));
		structureTypeStringMap.put("trave", resources.getString(R.string.trave));
		structureTypeStringMap.put("suspenso", resources.getString(R.string.suspenso));
		structureTypeStringMap.put("grade", resources.getString(R.string.grade));
		structureTypeStringMap.put("other", resources.getString(R.string.outro));

		ownershipImageMap = new Hashtable<>();
		ownershipImageMap.put("true",
				ResourcesCompat.getDrawable(resources, R.drawable.icon_public, null));
		ownershipImageMap.put("false",
				ResourcesCompat.getDrawable(resources, R.drawable.icon_private, null));

		ownershipStringMap = new Hashtable<>();
		ownershipStringMap.put("true", resources.getString(R.string.public_rack));
		ownershipStringMap.put("false", resources.getString(R.string.private_rack));
	}

	public static int getColorFromScore(float score) {

		int color;
		if (rating == 0) {
			color = ResourcesCompat.getColor(resources, R.color.mediumGray, null);
		} else if (rating > 0 && rating <= 2) {
			color = ResourcesCompat.getColor(resources, R.color.red, null);
		} else if (rating > 2 && rating < 3.5) {
			color = ResourcesCompat.getColor(resources, R.color.yellow, null);
		} else if (rating >= 3.5) {
			color = ResourcesCompat.getColor(resources, R.color.green, null);
		}
		return color;
	}

	public static BitmapDescriptor getCustomPin(float rackScore, boolean mini) {

		// Select correct resource
		Drawable drawable = null;
		if (rackScore == 0) {
			if (mini) {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_gray_mini, null);
			} else {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_gray, null);
			}
		} else if (rackScore > 0 && rackScore <= 2) {
			if (mini) {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_red_mini, null);
			} else {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_red, null);
			}
		} else if (rackScore > 2 && rackScore < 3.5) {
			if (mini) {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_yellow_mini, null);
			} else {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_yellow, null);
			}
		} else if (rackScore >= 3.5) {
			if (mini) {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_green_mini, null);
			} else {
				drawable = ResourcesCompat.getDrawable(resources, R.drawable.pin_green, null);
			}
		}

		float scale;
		int alpha;
		if (mini) {
			scale = rackScore == 0 ? 0.4f : 0.1f + (rackScore / 10);
			alpha = (int) (255 * 0.7);
		} else {
			scale = rackScore == 0 ? 0.8f : 0.6f + (rackScore / 10);
			alpha = (int) (255 * 0.9);
		}

		Bitmap bitmap;
		try {
			// Svg was way too big, that's why I'm dividing by seven
			// (mostly because I don't want to change the svgs too)
			bitmap = Bitmap.createBitmap(
					(int) (scale * drawable.getIntrinsicWidth() / 7),
					(int) (scale * drawable.getIntrinsicHeight() / 7),
					Bitmap.Config.ARGB_4444);

			Canvas canvas = new Canvas(bitmap);
			drawable.setAlpha(alpha);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
		} catch (OutOfMemoryError e) {
			return null;
		}
		return BitmapDescriptorFactory.fromBitmap(bitmap);
	}

	public static String getOwnershipString(String isPublic) {
		return isPublic != null ? ownershipStringMap.get(isPublic) : null;
	}

	public static Drawable getOwnershipImage(String isPublic) {
		return isPublic != null ? ownershipImageMap.get(isPublic) : null;
	}

	public static String getStructureTypeString(String structureType) {
		return structureType != null ? structureTypeStringMap.get(structureType) : null;
	}


	public static Drawable getStructureTypeImage(String structureType) {
		return structureType != null ? structureTypeImageMap.get(structureType) : null;
	}
}
