package stablemarriage.algorithms;

import java.util.*;

public class StableMarriage {
  public StableMarriage() {
    m_sMenList = new LinkedList();
    m_sWomenList = new LinkedList();
  }

  public StableMarriage(int iMenSize, int iWomenSize) {
    m_sMenList = new LinkedList();
    m_sWomenList = new LinkedList();
    m_iMenSize = iMenSize;
    m_iWomenSize = iWomenSize;
  }

  public void SetSize(int iMenSize, int iWomenSize) {
    m_iMenSize = iMenSize;
    m_iWomenSize = iWomenSize;
  }

  public boolean AddMan(Man aMan) {
    if (m_sMenList.size() >= m_iMenSize) {
      return false;
    }
    if ( (aMan.Islegal()) && (!aMan.IsMarried())) {
      return m_sMenList.add(aMan);
    }
    return false;

  }

  public boolean AddWoman(Woman aWoman) {
    if (m_sWomenList.size() >= m_iWomenSize) {
      return false;
    }
    if ( (aWoman.Islegal()) && (!aWoman.IsMarried())) {
      return m_sWomenList.add(aWoman);
    }
    return false;

  }

  protected void JustMarried(Man aMan, Woman aWoman) {
    aMan.SetMarried(true);
    aWoman.SetMarried(true);
    aMan.SetPartner(aWoman);
    aWoman.SetPartner(aMan);
  }

  protected void Devorce(Man aMan, Woman aWoman) {
    aMan.SetMarried(false);
    aWoman.SetMarried(false);
    aMan.SetPartner(null);
    aWoman.SetPartner(null);
  }

  public HashSet GetStableMarriage() {
    if ( (m_sMenList.size() != m_iMenSize) || (m_sWomenList.size() != m_iWomenSize)) {
      return null;
    }
    HashSet MarriedMen = new HashSet();
    while (MarriedMen.size() < m_iMenSize) {
      Man man = (Man) m_sMenList.getFirst();
      Woman woman = (Woman) (man.PopFirstPreference());
      if (woman == null) {
        m_sMenList.removeFirst();
        man.SetMarried(true);
        man.SetPartner(null);
        MarriedMen.add(man);
      }
      else {
        if (woman.IsMarried()) {
          if (woman.MorePrefer(man)) {
            //handle X husband
            Man womanXHusband = (Man) woman.GetPartner();
            Devorce(womanXHusband, woman);
            if (! (MarriedMen.remove(womanXHusband))) {
              System.out.println("Error in algorithm");
            }
            m_sMenList.removeFirst();
            m_sMenList.addFirst(womanXHusband);
            //Handling X hasband end
            JustMarried(man, woman);

            MarriedMen.add(man);
          }
        }
        else {
          JustMarried(man, woman);
          m_sMenList.removeFirst();
          MarriedMen.add(man);
        }
      }
    }
    return MarriedMen;
  }

  private LinkedList m_sMenList;
  private LinkedList m_sWomenList;
  private int m_iMenSize;
  private int m_iWomenSize;

}
