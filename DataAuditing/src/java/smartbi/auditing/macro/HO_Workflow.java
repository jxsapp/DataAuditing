package smartbi.auditing.macro;

import smartbi.macro.ClientSide;
import smartbi.macro.Events;
import smartbi.macro.Tips;
import smartbi.macro.scriptable.HO_Resource;

@Tips("${WorkFlow}")
@Events({ HE_afterAudit.class, HE_afterBackToPre.class, HE_afterBackToStart.class, HE_afterCommit.class,
		HE_afterRelease.class, HE_afterSummary.class, HE_afterVerify.class, HE_beforeAudit.class,
		HE_beforeBackToPre.class, HE_beforeBackToStart.class, HE_beforeCommit.class, HE_beforeRelease.class,
		HE_beforeSummary.class, HE_beforeVerify.class })
@ClientSide
public interface HO_Workflow extends HO_Resource {

}