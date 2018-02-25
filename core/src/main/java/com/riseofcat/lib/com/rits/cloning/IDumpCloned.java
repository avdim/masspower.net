package com.riseofcat.lib.com.rits.cloning;

import com.badlogic.gdx.utils.reflect.Field;

public interface IDumpCloned {
void startCloning(Class<?> clz);

void cloning(Field field, Class<?> clz);
}
