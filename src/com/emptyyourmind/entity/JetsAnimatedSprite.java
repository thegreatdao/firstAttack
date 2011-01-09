package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class JetsAnimatedSprite extends AnimatedSprite
{
	private SpriteOnPosistionChangedActionsAggregator slAggregator;
	
	public JetsAnimatedSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion, ILayer layer)
	{
		super(pX, pY, pTiledTextureRegion);
		layer.addEntity(this);
	}
	
	@Override
	protected void onPositionChanged()
	{
		super.onPositionChanged();
		if(slAggregator != null)
		{
			slAggregator.onPositionChanged();
		}
	}

	public void setSlAggregator(SpriteOnPosistionChangedActionsAggregator slAggregator)
	{
		this.slAggregator = slAggregator;
	}

	public void setiShootable(IShootable iShootable)
	{
		iShootable.shoot();
	}


}
