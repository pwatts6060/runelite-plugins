/*
 * Copyright (c) 2021, Matsyir <https://github.com/matsyir>
 * Copyright (c) 2020, Mazhar <https://twitter.com/maz_rs>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.lmsnotifier;

import java.util.HashSet;
import java.util.Set;

public enum AttackAnimation {
    // MELEE
    MELEE_DAGGER_SLASH(376), // tested w/ dds
    MELEE_SPEAR_STAB(381), // tested w/ zammy hasta
    MELEE_SWORD_STAB(386), // tested w/ dragon sword, obby sword, d long
    MELEE_SCIM_SLASH(390), // tested w/ rune & dragon scim, d sword, VLS, obby sword

    MELEE_GENERIC_SLASH(393), // tested w/ zuriel's staff, d long slash, dclaws regular slash
    MELEE_STAFF_CRUSH(0), // 393 previously, save name to support old fights but no longer track

    MELEE_BATTLEAXE_SLASH(395), // tested w/ rune baxe
    MELEE_MACE_STAB(400), // tested w/ d mace
    MELEE_BATTLEAXE_CRUSH(401), // tested w/ rune baxe, dwh & statius warhammer animation, d mace
    MELEE_2H_CRUSH(406), // tested w/ rune & dragon 2h
    MELEE_2H_SLASH(407), // tested w/ rune & dragon 2h
    MELEE_STAFF_CRUSH_2(414), // tested w/ ancient staff, 3rd age wand
    MELEE_STAFF_CRUSH_3(419), // Common staff crush. Air/fire/etc staves, smoke battlestaff, SOTD/SOL crush, zammy hasta crush
    MELEE_PUNCH(422),
    MELEE_KICK(423),
    MELEE_STAFF_STAB(428), // tested w/ SOTD/SOL jab, vesta's spear stab, c hally
    MELEE_SPEAR_CRUSH(429), // tested w/ vesta's spear
    MELEE_STAFF_SLASH(440), // tested w/ SOTD/SOL slash, zammy hasta slash, vesta's spear slash, c hally
    MELEE_DLONG_SPEC(1058), // tested w/ d long spec, also thammaron's sceptre crush (????)...
    MELEE_DRAGON_MACE_SPEC(1060),
    MELEE_DRAGON_DAGGER_SPEC(1062),
    MELEE_DRAGON_WARHAMMER_SPEC(1378), // tested w/ dwh, statius warhammer spec
    MELEE_ABYSSAL_WHIP(1658), // tested w/ whip, tent whip
    MELEE_GRANITE_MAUL(1665), // tested w/ normal gmaul, ornate maul
    MELEE_GRANITE_MAUL_SPEC(1667), // tested w/ normal gmaul, ornate maul
    MELEE_DHAROKS_GREATAXE_CRUSH(2066),
    MELEE_DHAROKS_GREATAXE_SLASH(2067),
    MELEE_AHRIMS_STAFF_CRUSH(2078),
    MELEE_OBBY_MAUL_CRUSH(2661),
    MELEE_ABYSSAL_DAGGER_STAB(3297),
    MELEE_ABYSSAL_BLUDGEON_CRUSH(3298),
    MELEE_LEAF_BLADED_BATTLEAXE_CRUSH(3852),
    MELEE_BARRELCHEST_ANCHOR_CRUSH(5865),
    MELEE_LEAF_BLADED_BATTLEAXE_SLASH(7004),
    MELEE_GODSWORD_SLASH(7045), // tested w/ AGS, BGS, ZGS, SGS, AGS(or) sara sword
    MELEE_GODSWORD_CRUSH(7054), // tested w/ AGS, BGS, ZGS, SGS, sara sword
    MELEE_DRAGON_CLAWS_SPEC(7514),
    MELEE_VLS_SPEC(7515), // both VLS and dragon sword spec
    MELEE_ELDER_MAUL(7516),
    MELEE_ZAMORAK_GODSWORD_SPEC(7638), // tested zgs spec
    MELEE_ZAMORAK_GODSWORD_OR_SPEC(7639), // UNTESTED, assumed due to ags(or)
    MELEE_SARADOMIN_GODSWORD_SPEC(7640), // tested sgs spec
    MELEE_SARADOMIN_GODSWORD_OR_SPEC(7641), // UNTESTED, assumed due to ags(or)
    MELEE_BANDOS_GODSWORD_SPEC(7642), // tested bgs spec
    MELEE_BANDOS_GODSWORD_OR_SPEC(7643), // UNTESTED, assumed due to ags(or)
    MELEE_ARMADYL_GODSWORD_SPEC(7644), // tested ags spec
    MELEE_ARMADYL_GODSWORD_OR_SPEC(7645), // tested ags(or) spec
    MELEE_SCYTHE(8056), // tested w/ all scythe styles (so could be crush, but unlikely)
    MELEE_GHAZI_RAPIER_STAB(8145), // rapier slash is 390, basic slash animation. Also VLS stab.
    MELEE_ANCIENT_GODSWORD_SPEC(9171),

    // RANGED
    RANGED_SHORTBOW(426), // Confirmed same w/ 3 types of arrows, w/ maple, magic, & hunter's shortbow, craw's bow, dbow, dbow spec
    RANGED_RUNE_KNIFE_PVP(929), // 1 tick animation, has 1 tick delay between attacks. likely same for all knives. Same for morrigan's javelins, both spec & normal attack.
    RANGED_MAGIC_SHORTBOW_SPEC(1074),
    RANGED_CROSSBOW_PVP(4230), // Tested RCB & ACB w/ dragonstone bolts (e) & diamond bolts (e)
    RANGED_BLOWPIPE(5061), // tested in PvP with all styles. Has 1 tick delay between animations in pvp.
    RANGED_DARTS(6600), // tested w/ addy darts. Seems to be constant animation but sometimes stalls and doesn't animate
    RANGED_BALLISTA(7218), // Tested w/ dragon javelins.
    RANGED_DRAGON_THROWNAXE_SPEC(7521),
    RANGED_RUNE_CROSSBOW(7552),
    RANGED_BALLISTA_2(7555), // tested w/ light & heavy ballista, dragon & iron javelins.
    RANGED_RUNE_KNIFE(7617), // 1 tick animation, has 1 tick delay between attacks. Also d thrownaxe
    RANGED_DRAGON_KNIFE(8194),
    RANGED_DRAGON_KNIFE_POISONED(8195), // tested w/ d knife p++
    RANGED_DRAGON_KNIFE_SPEC(8292),
    RANGED_ZARYTE_CROSSBOW(9168),
    RANGED_ZARYTE_CROSSBOW_PVP(9166),

    // MAGIC - uses highest base damage available when animations are re-used. No damage = 0 damage.
    // for example, strike/bolt/blast animation will be fire blast base damage, multi target ancient spells will be ice barrage.
    MAGIC_STANDARD_BIND(710), // tested w/ bind, snare, entangle
    MAGIC_STANDARD_STRIKE_BOLT_BLAST(711), // tested w/ bolt
    MAGIC_STANDARD_BIND_STAFF(1161), // tested w/ bind, snare, entangle, various staves
    MAGIC_STANDARD_STRIKE_BOLT_BLAST_STAFF(1162), // strike, bolt and blast (tested all spells, different weapons)
    MAGIC_STANDARD_WAVE_STAFF(1167), // tested many staves
    MAGIC_STANDARD_SURGE_STAFF(7855), // tested many staves
    MAGIC_ANCIENT_SINGLE_TARGET(1978), // Rush & Blitz animations (tested all 8, different weapons)
    MAGIC_ANCIENT_MULTI_TARGET(1979), // Burst & Barrage animations (tested all 8, different weapons)
    MAGIC_VOLATILE_NIGHTMARE_STAFF_SPEC(8532), // assume 99 mage's base damage (does not rise when boosted).
    ;

    public static final Set<Integer> anims = new HashSet<>();
    static {
        for (AttackAnimation data : values()) {
            anims.add(data.animId);
        }
    }

    public final int animId;

    AttackAnimation(int animId) {
        this.animId = animId;
    }

    static boolean contains(int animId) {
        return anims.contains(animId);
    }
}
