package com.emptyyourmind.entity;

import java.util.HashSet;
import java.util.Set;

public class SpriteOnPosistionChangedActionsAggregator implements IPositionChangedListener
{
	private Set<ICollidable> iCollidables;
	private IPositionChangedListener iPositionChangedListener;
	private IResetable iResetable;
	
	@Override
	public void onPositionChanged()
	{
		if(iPositionChangedListener != null)
		{
			iPositionChangedListener.onPositionChanged();
		}
		if(iCollidables != null)
		{
			for(ICollidable iCollidable : iCollidables)
			{
				iCollidable.collide();
			}
		}
	}

	public void setiPositionChangedListener(IPositionChangedListener iPositionChangedListener)
	{
		this.iPositionChangedListener = iPositionChangedListener;
	}

	public void addCollidable(ICollidable iCollidable)
	{
		if(iCollidables == null)
		{
			iCollidables = new HashSet<ICollidable>();
		}
		iCollidables.add(iCollidable);
	}

	public void setiResetable(IResetable iResetable)
	{
		this.iResetable = iResetable;
	}

	public IResetable getiResetable()
	{
		return iResetable;
	}
	
}
