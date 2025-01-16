package servicebook.localization;

public enum Localization {

    /**
     * Англійська мова
     */
    EN {
        @Override
        public String getValue(LocalizedString localized) {
            return localized.getEn();
        }

        @Override
        public void setValue(LocalizedString localized, String value) {
            localized.setEn(value);
        }
    },
    /**
     * Українська мова
     */
    UA {

        @Override
        public String getValue(LocalizedString localized) {
            return localized.getUa();
        }

        @Override
        public void setValue(LocalizedString localized, String value) {
            localized.setUa(value);
        }
    };

    public abstract String getValue(LocalizedString localized);
    public abstract void setValue(LocalizedString localized, String value);
}
