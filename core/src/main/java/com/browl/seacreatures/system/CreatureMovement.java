package com.browl.seacreatures.system;

import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.model.MovementStyle;

import java.util.Random;

/** Simple procedural wandering movement so the aquarium never looks static. */
public class CreatureMovement {
    private final Random random = new Random();

    public void update(Friend friend, float deltaSeconds, float minX, float maxX, float minY, float maxY) {
        MovementStyle style = friend.getCreatureType().getMovementStyle();
        float speed;
        switch (style) {
            case DART: speed = 90f; break;
            case SCUTTLE: speed = 40f; break;
            case GLIDE: speed = 35f; break;
            case PULSE: speed = 15f; break;
            default: speed = 25f;
        }

        // Occasionally pick a new wander direction.
        if (random.nextFloat() < deltaSeconds * 0.4f) {
            friend.setWanderAngle(random.nextFloat() * 360f);
        }

        float radians = (float) Math.toRadians(friend.getWanderAngle());
        float dx = (float) Math.cos(radians) * speed * deltaSeconds;
        float dy = style == MovementStyle.SCUTTLE ? 0 : (float) Math.sin(radians) * speed * deltaSeconds * 0.4f;

        float nx = friend.getX() + dx;
        float ny = friend.getY() + dy;

        if (nx < minX || nx > maxX) {
            friend.setWanderAngle(180f - friend.getWanderAngle());
            nx = Math.max(minX, Math.min(maxX, nx));
        }
        if (ny < minY || ny > maxY) {
            friend.setWanderAngle(-friend.getWanderAngle());
            ny = Math.max(minY, Math.min(maxY, ny));
        }

        friend.setX(nx);
        friend.setY(ny);
    }
}
