package com.example.hemolinkbackend.service.support;

import com.example.hemolinkbackend.enums.GroupeSanguin;

import java.util.List;
import java.util.Map;

public final class CompatibiliteSanguineUtils {

    private static final Map<GroupeSanguin, List<GroupeSanguin>> COMPATIBILITES = Map.of(
            GroupeSanguin.O_NEG, List.of(GroupeSanguin.O_NEG),
            GroupeSanguin.O_POS, List.of(GroupeSanguin.O_POS, GroupeSanguin.O_NEG),
            GroupeSanguin.A_NEG, List.of(GroupeSanguin.A_NEG, GroupeSanguin.O_NEG),
            GroupeSanguin.A_POS, List.of(GroupeSanguin.A_POS, GroupeSanguin.A_NEG, GroupeSanguin.O_POS, GroupeSanguin.O_NEG),
            GroupeSanguin.B_NEG, List.of(GroupeSanguin.B_NEG, GroupeSanguin.O_NEG),
            GroupeSanguin.B_POS, List.of(GroupeSanguin.B_POS, GroupeSanguin.B_NEG, GroupeSanguin.O_POS, GroupeSanguin.O_NEG),
            GroupeSanguin.AB_NEG, List.of(GroupeSanguin.AB_NEG, GroupeSanguin.A_NEG, GroupeSanguin.B_NEG, GroupeSanguin.O_NEG),
            GroupeSanguin.AB_POS, List.of(GroupeSanguin.values())
    );

    private CompatibiliteSanguineUtils() {
    }

    public static List<GroupeSanguin> groupesCompatiblesPourReceveur(GroupeSanguin groupeReceveur) {
        return COMPATIBILITES.getOrDefault(groupeReceveur, List.of(groupeReceveur));
    }
}

