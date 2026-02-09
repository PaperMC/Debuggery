/*
 * This file is part of Debuggery.
 *
 * Debuggery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Debuggery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Debuggery.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.zachbr.debuggery.reflection.bukkit;

import io.zachbr.debuggery.Logger;
import io.zachbr.debuggery.TestLoggerImpl;
import io.zachbr.debuggery.reflection.types.InputException;
import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.bukkit.BukkitBootstrap;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class BukkitInputHandlerTest {
    private final Logger logger = new TestLoggerImpl();
    private final TypeHandler typeHandler = new TypeHandler(logger);

    public BukkitInputHandlerTest() {
        new BukkitBootstrap(typeHandler, logger);
    }

    @Test
    public void testMaterial() throws InputException {
        Class[] inputTypes = {Material.class, Material.class, Material.class};
        String[] input = {"gold_block", "DIAMOND_SWORD", "blaze_ROD"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof Material);
        }

        // Finally, let's make sure the values are correct
        assertSame(Material.GOLD_BLOCK, output[0]);
        assertSame(Material.DIAMOND_SWORD, output[1]);
        assertSame(Material.BLAZE_ROD, output[2]);
    }

    @Test
    public void testArrayItemStacks() throws InputException {
        Class[] inputTypes = {ItemStack[].class};
        String[] input = {"diamond,sand,diamond_sword"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // ensure length of input is the same as output
        assertEquals(inputTypes.length, output.length);

        // ensure output is of proper type
        for (Object object : output) {
            assertTrue(object instanceof ItemStack[]);
        }

        ItemStack[] itemStackArr = (ItemStack[]) output[0];

        // verify types in array
        assertSame(Material.DIAMOND, itemStackArr[0].getType());
        assertSame(Material.SAND, itemStackArr[1].getType());
        assertSame(Material.DIAMOND_SWORD, itemStackArr[2].getType());
    }

    @Test
    public void testItemStack() throws InputException {
        Class[] inputTypes = {ItemStack.class};
        String[] input = {"diamond"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof ItemStack);
        }

        // Finally, let's make sure the values are correct
        assertSame(Material.DIAMOND, ((ItemStack) output[0]).getType());
    }

    @SuppressWarnings("deprecation")
    @Test
    @Disabled(value = "No handler for MaterialData exists anymore")
    public void testMaterialData() throws InputException {
        Class[] inputTypes = {MaterialData.class};
        String[] input = {"diamond_shovel:24"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof MaterialData);
        }

        // Finally, let's make sure the values are correct
        assertSame(Material.DIAMOND_SHOVEL, ((MaterialData) output[0]).getItemType());
    }

    @Test
    public void testGameMode() throws InputException {
        Class[] inputTypes = {GameMode.class, GameMode.class};
        String[] input = {"1", "adventure"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof GameMode);
        }

        // Finally, let's make sure the values are correct
        assertSame(GameMode.CREATIVE, output[0]);
        assertSame(GameMode.ADVENTURE, output[1]);
    }

    @Test
    public void testDifficulty() throws InputException {
        Class[] inputTypes = {Difficulty.class, Difficulty.class};
        String[] input = {"3", "peaceful"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof Difficulty);
        }

        // Finally, let's make sure the values are correct
        assertSame(Difficulty.HARD, output[0]);
        assertSame(Difficulty.PEACEFUL, output[1]);
    }

    @Test
    public void testBukkitClasses() throws InputException {
        Class[] inputTypes = {Class[].class};
        String[] input = {"Zombie,Creeper,Pig"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        // First let's make sure we didn't lose anything, or get anything
        assertEquals(inputTypes.length, output.length);

        // Next let's make sure everything is the right type
        for (Object object : output) {
            assertTrue(object instanceof Class[]);
        }

        // Finally, let's make sure the values are correct
        Class[] classes = (Class[]) output[0];
        assertEquals(classes[0], Zombie.class);
        assertEquals(classes[1], Creeper.class);
        assertEquals(classes[2], Pig.class);
    }

    @Test
    public void testPotionEffectType() throws InputException {
        Class[] inputTypes = {PotionEffectType.class, PotionEffectType.class, PotionEffectType.class};
        String[] input = {"NIGHT_VISION", "speed", "iNviSiBilIty"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        assertEquals(inputTypes.length, output.length);

        for (Object object : output) {
            assertTrue(object instanceof PotionEffectType);
        }

        assertSame(PotionEffectType.NIGHT_VISION, output[0]);
        assertSame(PotionEffectType.SPEED, output[1]);
        assertSame(PotionEffectType.INVISIBILITY, output[2]);

        // Test throws when no type exists
        Assertions.assertThrows(InputException.class, () -> typeHandler.instantiateTypes(new Class[]{PotionEffectType.class}, Collections.singletonList("doesntExist")));
    }

    @Test
    public void testPotionEffect() throws InputException {
        Class[] inputTypes = {PotionEffect.class, PotionEffect.class, PotionEffect.class};
        String[] input = {"NIGHT_VISION", "speed,200,5", "iNviSiBilIty,150,2"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        assertEquals(inputTypes.length, output.length);

        for (Object object : output) {
            assertTrue(object instanceof PotionEffect);
        }

        PotionEffect effect0 = (PotionEffect) output[0];
        PotionEffect effect1 = (PotionEffect) output[1];
        PotionEffect effect2 = (PotionEffect) output[2];

        // basic case, all we guarantee is the type
        assertSame(PotionEffectType.NIGHT_VISION, effect0.getType());

        // secondary case, we guarantee type and duration
        assertSame(PotionEffectType.SPEED, effect1.getType());
        assertEquals(200, effect1.getDuration());

        // tertiary case, we guarantee type, duration, and amplifer
        assertSame(PotionEffectType.INVISIBILITY, effect2.getType());
        assertEquals(150, effect2.getDuration());
        assertEquals(2, effect2.getAmplifier());
    }

    @Test
    public void testEulerAngle() throws InputException {
        Class[] inputTypes = {EulerAngle.class, EulerAngle.class};
        String[] input = {"1,2,3", "3.45,1.22,4.12"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        assertEquals(inputTypes.length, output.length);

        for (Object object : output) {
            assertTrue(object instanceof EulerAngle);
        }

        // test values are well within JVM guarantees
        final double delta = Double.MIN_VALUE;

        EulerAngle angle1 = (EulerAngle) output[0];
        EulerAngle angle2 = (EulerAngle) output[1];

        assertEquals(1, angle1.getX(), delta);
        assertEquals(2, angle1.getY(), delta);
        assertEquals(3, angle1.getZ(), delta);

        assertEquals(3.45, angle2.getX(), delta);
        assertEquals(1.22, angle2.getY(), delta);
        assertEquals(4.12, angle2.getZ(), delta);
    }

    @Test
    public void testVector() throws InputException {
        Class[] inputTypes = {org.bukkit.util.Vector.class, org.bukkit.util.Vector.class};
        String[] input = {"1.22,4.512,4.122", "1,2,3"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        assertEquals(inputTypes.length, output.length);

        for (Object object : output) {
            assertTrue(object instanceof org.bukkit.util.Vector);
        }

        // test values are well within JVM guarantees
        final double delta = Double.MIN_VALUE;

        org.bukkit.util.Vector vector1 = (org.bukkit.util.Vector) output[0];
        org.bukkit.util.Vector vector2 = (org.bukkit.util.Vector) output[1];

        assertEquals(1.22, vector1.getX(), delta);
        assertEquals(4.512, vector1.getY(), delta);
        assertEquals(4.122, vector1.getZ(), delta);

        assertEquals(1, vector2.getX(), delta);
        assertEquals(2, vector2.getY(), delta);
        assertEquals(3, vector2.getZ(), delta);
    }

    @Test
    public void testNamespacedKey() throws InputException {
        Class[] inputTypes = {NamespacedKey.class, NamespacedKey.class};
        String[] input = {"grass_block", "tacos:pizza"};

        Object[] output = typeHandler.instantiateTypes(inputTypes, Arrays.asList(input));

        assertEquals(inputTypes.length, output.length);

        for (Object object : output) {
            assertTrue(object instanceof NamespacedKey);
        }

        NamespacedKey implied = (NamespacedKey) output[0];
        NamespacedKey explict = (NamespacedKey) output[1];

        assertEquals("minecraft", implied.getNamespace());
        assertEquals("grass_block", implied.getKey());
        assertEquals("tacos", explict.getNamespace());
        assertEquals("pizza", explict.getKey());
    }
}
