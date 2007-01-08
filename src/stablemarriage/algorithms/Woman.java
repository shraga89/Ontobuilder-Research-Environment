package stablemarriage.algorithms;

public class Woman
    extends StableMarriagePlayer {
  public Woman(int nPreferencesSize) {
    super(nPreferencesSize);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param nPreferencesSize
   * @param sName
   */
  public Woman(int nPreferencesSize, String sName) {
    super(/*nPreferencesSize, */sName);
    // TODO Auto-generated constructor stub
  }

  public boolean MorePrefer(StableMarriagePlayer candidate) {
    int currPartnerIndex = m_lPreferencesList.indexOf(m_uPartner);
    int candIndex = m_lPreferencesList.indexOf(candidate);

    //if the proposed partner appear before the current partner in the list
    //then 'this' prefer the proposed partner on its current partner;
    if (candIndex < currPartnerIndex) {
      return true;
    }
    return false;
  }

}
