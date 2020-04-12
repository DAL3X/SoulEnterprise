package de.dal3x.enterprise.company;

import java.util.UUID;

import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;

public class CompanyHelper {

    public static boolean isEmployee(UUID id) {
        return Filehandler.hasPlayerData(id);
    }

    public static boolean companyExists(String name) {
        if (!Filehandler.hasCompanyFile(name)) {
            return false;
        }
        Filehandler.loadCompany(name);
        if (EnterprisePlugin.getInstance().getCompanyForName(name) != null) {
            return true;
        }
        return false;
    }

    public static boolean isCEO(UUID id) {
        if (EnterprisePlugin.getInstance().getCompanyForPlayerID(id) == null) {
            return false;
        }
        return id.equals(EnterprisePlugin.getInstance().getCompanyForPlayerID(id).getCeo());
    }

    public static boolean isManagement(UUID id) {
        return isCEO(id) || isCOO(id);
    }

    public static boolean isCOO(UUID id) {
        if (EnterprisePlugin.getInstance().getCompanyForPlayerID(id) == null) {
            return false;
        }
        return EnterprisePlugin.getInstance().getCompanyForPlayerID(id).getManager().contains(id);
    }

}
