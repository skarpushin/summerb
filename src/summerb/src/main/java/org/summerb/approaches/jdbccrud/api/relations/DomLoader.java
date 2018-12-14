package org.summerb.approaches.jdbccrud.api.relations;

import java.util.List;
import java.util.Set;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;

/**
 * Interface used to load Dom object trees. This might be more convenient than
 * dealing with {@link DataSet} and {@link DataSetLoader}, but it will require
 * creation of classes which will represent domain object model.
 * 
 * <p>
 * 
 * See example: TBD (write article on wiki and put link here)
 * 
 * <p>
 * 
 * High-level pre-requisites:
 * <ul>
 * <li>for each Dom entity you need to create class</li>
 * <li>class for Dom entity must extend Row class</li>
 * <li>Dom entity can have fields to hold referenced Dom entities (could be
 * direct reference to 1 instance or it could be a list)</li>
 * <li>Name of the field (references) is used to calculate reference name. If
 * class "Env" contains field "List&lt;Device&gt; devices", then it's expected
 * to have reference with name "envDevices"</li>
 * </ul>
 * 
 * @author sergeyk
 *
 */
public interface DomLoader {

	<TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> TDomClass load(Class<TDomClass> rootDomClass,
			TId rootDtoId, Ref... refsToResolve);

	<TId, TRowClass extends HasId<TId>, TDomClass extends TRowClass> List<TDomClass> load(Class<TDomClass> rootDomClass,
			Set<TId> ids, Ref... refsToResolve);

}