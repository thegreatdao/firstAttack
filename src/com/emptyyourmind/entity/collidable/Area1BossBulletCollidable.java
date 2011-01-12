package com.emptyyourmind.entity.collidable;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.emptyyourmind.entity.ICollidable;
import com.emptyyourmind.entity.JetsAnimatedSprite;
import com.emptyyourmind.entity.JetsFightConstants;
import com.emptyyourmind.entity.JetsSprite;
import com.emptyyourmind.utils.JetsFightUtils;

public class Area1BossBulletCollidable implements ICollidable
{
	private JetsAnimatedSprite player;
	private JetsSprite bullet;
	private BaseGameActivity baseGameActivity;
	private ILayer layer;
	private int damage;
	
	public Area1BossBulletCollidable(JetsSprite bullet, JetsAnimatedSprite player, BaseGameActivity baseGameActivity, ILayer layer, int damage)
	{
		this.player = player;
		this.bullet = bullet;
		this.baseGameActivity = baseGameActivity;
		this.layer = layer;
		this.damage = damage;
	}
	
	@Override
	public void collide()
	{
		final float centerX = bullet.getX() + JetsFightConstants.BULLET_AREA1_BOSS_RADIUS;
		final float centerY = bullet.getY() + JetsFightConstants.BULLET_AREA1_BOSS_DISTANCE_BTW_TOP_AND_CENTER;
		final float radius = JetsFightConstants.BULLET_AREA1_BOSS_RADIUS;
		
		final float width = player.getWidth();
		final float xMid = player.getX() + width / 2.0f;
		final float halfVerticalWidth = JetsFightConstants.PLAYER_VERTICAL_BODY_WIDTH / 2.0f;
		final float xTop = xMid - halfVerticalWidth;
		final float yLeft = player.getY();
		final float xBottom = xMid + halfVerticalWidth;
		final float yRight = player.getY() + JetsFightConstants.PLAYER_VERTICAL_BODY_HEIGHT;
		
		final float halfHorizontalWidth = JetsFightConstants.PLAYER_HORIZONTAL_BODY_WIDTH / 2.0f;
		final float xTopH = xMid - halfHorizontalWidth;
		final float yLeftH = player.getY() - 1;
		final float xBottomH = xMid + halfHorizontalWidth;
		final float yRightH = player.getY() + JetsFightConstants.PLAYER_HORIZONTAL_BODY_HEIGHT;
		
		if (JetsFightUtils.ciricleCollidesWithRectangle(centerX, centerY, radius, xTop, yLeft, xBottom, yRight)
				|| JetsFightUtils.ciricleCollidesWithRectangle(centerX, centerY, radius, xTopH, yLeftH, xBottomH, yRightH))
		{
			final JetsAnimatedSprite player = Area1BossBulletCollidable.this.player;
			if(!player.isDead())
			{
				baseGameActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						player.damage(damage);
						Area1BossBulletCollidable.this.layer.removeEntity(bullet);
					}
				});
			}
			if(player.isDead())
			{
				baseGameActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						Area1BossBulletCollidable.this.layer.removeEntity(player);
					}
				});
			}
		}
	}

}
