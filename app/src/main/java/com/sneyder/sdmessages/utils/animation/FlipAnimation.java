/*
 * Copyright (C) 2018 Sneyder Angulo.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sneyder.sdmessages.utils.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**  
 * This class extends Animation to support a 3D flip view transition animation. Two instances of this class is 
 * required: one for the "from" view and another for the "to" view. 
 * 
 * NOTE: use {@link AnimationFactory} to use this class.
 * 
 *  @author Ephraim A. Tekle
 *
 */
public class FlipAnimation extends Animation {
    public static final int ROTATION_X = 0;
    public static final int ROTATION_Y = 1;
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private Camera mCamera;
    private int mDirection;
	
	private final ScaleUpDownEnum scaleType;
	 
	/**
	 * How much to scale up/down. The default scale of 75% of full size seems optimal based on testing. Feel free to experiment away, however.
	 */ 
	public static final float SCALE_DEFAULT = 0.75f;
	
	private float scale;

	/**
	 * Constructs a new {@code FlipAnimation} object.Two {@code FlipAnimation} objects are needed for a complete transition b/n two views. 
	 * 
	 * @param fromDegrees the start angle in degrees for a rotation along the y-axis, i.e. in-and-out of the screen, i.e. 3D flip. This should really be multiple of 90 degrees.
	 * @param toDegrees the end angle in degrees for a rotation along the y-axis, i.e. in-and-out of the screen, i.e. 3D flip. This should really be multiple of 90 degrees.
	 * @param centerX the x-axis value of the center of rotation
	 * @param centerY the y-axis value of the center of rotation
	 * @param scale to get a 3D effect, the transition views need to be zoomed (scaled). This value must be b/n (0,1) or else the default scale {@link #SCALE_DEFAULT} is used.
	 * @param scaleType flip view transition is broken down into two: the zoom-out of the "from" view and the zoom-in of the "to" view. This parameter is used to determine which is being done. See {@link ScaleUpDownEnum}.
	 */
	public FlipAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float scale, ScaleUpDownEnum scaleType) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		this.scale = (scale<=0||scale>=1)?SCALE_DEFAULT:scale;
		this.scaleType = scaleType==null? ScaleUpDownEnum.SCALE_CYCLE:scaleType;
        mDirection = ROTATION_Y;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();

        if (mDirection == ROTATION_X)
            camera.rotateX(degrees);
        else
            camera.rotateY(degrees);

		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY); 
		
		matrix.preScale(scaleType.getScale(scale, interpolatedTime), scaleType.getScale(scale, interpolatedTime), centerX, centerY);

	}

    /**
     * Get the current direction, it can be {@link #ROTATION_X} or {#ROTATION_Y}
     */
    public void setDirection(int direction) {
        mDirection = direction;
    }

	/**
	 * This enumeration is used to determine the zoom (or scale) behavior of a {@link FlipAnimation}.
	 * 
	 * @author Ephraim A. Tekle 
	 *
	 */
	public enum ScaleUpDownEnum {
		/**
		 * The view will be scaled up from the scale value until it's at 100% zoom level (i.e. no zoom).
		 */
		SCALE_UP, 
		/**
		 * The view will be scaled down starting at no zoom (100% zoom level) until it's at a specified zoom level.
		 */
		SCALE_DOWN, 
		/**
		 * The view will cycle through a zoom down and then zoom up.
		 */
		SCALE_CYCLE, 
		/**
		 * No zoom effect is applied.
		 */
		SCALE_NONE;
		
		/**
		 * The intermittent zoom level given the current or desired maximum zoom level for the specified iteration
		 * 
		 * @param max the maximum desired or current zoom level
		 * @param iter the iteration (from 0..1).
		 * @return the current zoom level
		 */
		public float getScale(float max, float iter) {
			switch(this) {
			case SCALE_UP:
				return max +  (1-max)*iter;
				
			case SCALE_DOWN:
				return 1 - (1-max)*iter;
				
			case SCALE_CYCLE: { 
				final boolean halfWay = (iter > 0.5);  

				if (halfWay) {
					return max +  (1-max)*(iter-0.5f)*2;
				} else {
					return 1 - (1-max)*(iter*2);
				}
			}
			
			default:
				return 1;
			}
		}
	}
	
}
