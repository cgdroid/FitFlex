package com.tmhnry.fitflex.model;

public class Belly {
    public enum Type {
        BA(0), BB(1), IA(2), IB(3), AA(4), AB(5);
        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Type getType(int code) {
            switch (code) {
                case 0:
                    return BA;
                case 1:
                    return BB;
                case 2:
                    return IA;
                case 3:
                    return IB;
                case 4:
                    return AA;
                default:
                    return AB;
            }
        }
    }
}
