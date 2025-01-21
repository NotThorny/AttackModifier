package thorny.grasscutters.AttackModifier.utils;

import com.google.gson.annotations.SerializedName;

public class CharacterAvatar {
    @SerializedName("skill")
    public SkillIds skill;

    // Constructor for fully provided skill data
    public CharacterAvatar(int a, int b, int c) {
        this.skill = new SkillIds(a, b, c);
    }

    public SkillIds getSkills() {
        return this.skill;
    }

    public void setSkills(SkillIds skills) {
        this.skill = skills;
    }

    @Override
    public String toString() {
        return this.skill.toString();
    }

    // Ids for the skills to be used by the character
    public static class SkillIds {
        @SerializedName("normalAtk")
        private int normalAtk;
        @SerializedName("elementalSkill")
        private int elementalSkill;
        @SerializedName("elementalBurst")
        private int elementalBurst;

        public SkillIds(int i, int j, int k) {
            this.normalAtk = i;
            this.elementalSkill = j;
            this.elementalBurst = k;
        }

        public SkillIds() {
            this.normalAtk = 0;
            this.elementalSkill = 0;
            this.elementalBurst = 0;
        }

        public void setNormalAtk(int normalAtk) {
            this.normalAtk = normalAtk;
        }

        public void setElementalSkill(int elementalSkill) {
            this.elementalSkill = elementalSkill;
        }

        public void setElementalBurst(int elementalBurst) {
            this.elementalBurst = elementalBurst;
        }

        public int normalAtk() {
            return normalAtk;
        }

        public int elementalSkill() {
            return elementalSkill;
        }

        public int elementalBurst() {
            return elementalBurst;
        }

        @Override
        public String toString() {
            return "{ " + this.normalAtk + ", " + this.elementalSkill + ", " + this.elementalBurst + " }";
        }
    }
}
