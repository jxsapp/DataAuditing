package smartbi.auditing.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;


import smartbi.net.sf.json.JSONArray;
import smartbi.net.sf.json.JSONObject;
import smartbi.repository.AbstractDAO;
import smartbi.repository.DAOModule;
import smartbi.repository.HibernateUtil;
import smartbi.repository.Repository;
import smartbi.util.DBType;
import smartbi.util.UUIDGenerator;

public class WorkflowResourceDao extends AbstractDAO<WorkflowResource, String> {
	/** */
	private static WorkflowResourceDao instance = new WorkflowResourceDao();

	/**
	 * 
	 * @return static
	 */
	public static WorkflowResourceDao getInstance() {
		return instance;
	}

	/** */
	WorkflowResourceDao() {
		super(DAOModule.getInstance());
	}

	/**
	 * 
	 * @param id
	 *            id
	 * @return WorkflowResource
	 */
	public List<WorkflowResource> getByWorkflowId(String id) {
		return findByNamedQuery("WorkflowRes.getByWorkflowId", id);
	}
	
	/**
	 * 
	 * @param mechanismParam mechanismParam
	 * @return List<WorkflowResource>
	 */
	public List<WorkflowResource> getByMechanismParam(String mechanismParam) {
		return findByNamedQuery("WorkflowRes.getByMechanismParam", mechanismParam);
	}

	/**
	 * 
	 * @param otherParams otherParams
	 * @return List<WorkflowResource>
	 */
	public List<WorkflowResource> getByOtherParams(String otherParams) {
		Repository repository = DAOModule.getInstance().getRepository();
		DBType dbType = repository.getDatabaseType();
		if (dbType != DBType.MYSQL) {
			List<WorkflowResource> query = findByNamedQuery("WorkflowRes.getByOtherParamsNotMySQL");
			@SuppressWarnings("unchecked")
			List<WorkflowResource> result = new ArrayList();
			if (query != null) {
				for (WorkflowResource resource : query) {
					if (resource.getOtherParams().equals(otherParams)) {
						result.add(resource);
					}
				}
			}
			return result;
		} else {
			return findByNamedQuery("WorkflowRes.getByOtherParams", otherParams);
		}
	}
	

	/**
	 * 
	 * @param wrokflowId wrokflowId
	 * @param mechanismParam mechanismParam
	 * @param otherParams otherParams
	 * @return List<WorkflowResource>
	 */
	public List<WorkflowResource> getByWorkflowAndParam(String wrokflowId, String mechanismParam, String otherParams) {
		Repository repository = DAOModule.getInstance().getRepository();
		DBType dbType = repository.getDatabaseType();
		if (dbType != DBType.MYSQL) {
			List<WorkflowResource> query = findByNamedQuery("WorkflowRes.getByWorkflowAndParamNotMySQL", wrokflowId,
					mechanismParam);
			@SuppressWarnings("unchecked")
			List<WorkflowResource> result = new ArrayList();
			if (query != null) {
				for (WorkflowResource resource : query) {
					if (resource.getOtherParams().equals(otherParams)) {
						result.add(resource);
					}
				}
			}
			return result;
		} else {
			return findByNamedQuery("WorkflowRes.getByWorkflowAndParam", wrokflowId,
					mechanismParam, otherParams);
		}
	}

	/**
	 * 
	 * @param spreadSheetId spreadSheetId
	 * @param mechanismParam mechanismParam
	 * @param otherParams otherParams
	 * @return WorkflowResource
	 */
	public WorkflowResource getBySpreadSheetAndParam(String spreadSheetId, String mechanismParam, String otherParams) {
		Repository repository = DAOModule.getInstance().getRepository();
		DBType dbType = repository.getDatabaseType();
		@SuppressWarnings("unchecked")
		List<WorkflowResource> objs = new ArrayList();
		if (dbType != DBType.MYSQL) {
			List<WorkflowResource> query = findByNamedQuery("WorkflowRes.getBySpreadSheetAndParamNotMySQL", spreadSheetId,
					mechanismParam);
			if (query != null) {
				for (WorkflowResource resource : query) {
					if (resource.getOtherParams().equals(otherParams)) {
						objs.add(resource);
					}
				}
			}
		} else {
			objs = findByNamedQuery("WorkflowRes.getBySpreadSheetAndParam", spreadSheetId,
					mechanismParam, otherParams);
		}
		
		if (objs.size() >= 1) {
			return objs.get(0);
		} else {
			return null;
		}
	}
	/**
	 * 
	 * @param spreadsheetId
	 *            spreadsheetId
	 * @param workflowId
	 *            workflowId
	 * @param parameters
	 *            parameters
	 * @return String
	 */
	public String setResource(String spreadsheetId, String workflowId, String parameters) {
		WorkflowResource obj = new WorkflowResource();
		obj.setResourceId(UUIDGenerator.generate());
		return this.save(obj);
	}
	
	/**
	 * 
	 * @param workflowId 
	 * @param mechanismParam 
	 * @param otherParams 
	 * @return List<WorkflowResource> 
	 */
	public List<WorkflowResource> findWorkflowResource(String workflowId, String mechanismParam, String otherParams) {
		String sql = "select res from WorkflowResource res where  res.workflowId = '" + workflowId + "'";
		if (mechanismParam != null && !mechanismParam.equals("[]")) {
			sql += " and res.mechanismParam like '%" + mechanismParam + "%'";
		}
		DBType dbType = daoModule.getRepository().getDatabaseType();
		Set<String> oParams = new HashSet<String>();
		if (otherParams != null) {
			JSONArray otherParamsJson = JSONArray.fromString(otherParams);
			Iterator it = otherParamsJson.iterator();
			while (it.hasNext()) {
				JSONObject obj = (JSONObject) it.next();
				if (dbType == DBType.GBASE8T) {
					oParams.add(obj.toString());
				} else {

					sql += " and res.otherParams like '%" + obj.toString() + "%'";
				}
			}
		}
		Session session = HibernateUtil.currentSession();
		List<WorkflowResource> resources = (List<WorkflowResource>) session.createQuery(sql).list();
		//GBASE8T 过滤
		if (dbType == DBType.GBASE8T && oParams.size() > 0) {
			List<WorkflowResource>  result = new ArrayList<WorkflowResource>();
			for (WorkflowResource res : resources) {
				String p = res.getOtherParams();
				if (p != null) {
					boolean andlikes = true;
					for (String o : oParams) {
						if (!p.contains(o)) {
							andlikes = false;
							break;
						}
					}
					if (andlikes) {
						result.add(res);
					}
				}
			}
			return result;
		}
		return resources;
	}
}
