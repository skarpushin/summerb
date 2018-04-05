package org.summerb.approaches.jdbccrud.scaffold.api;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.dto.HasId;

/**
 * This gives you a quick start for EasyCrud back-end functionality. It will
 * bootstrap Dao and Service and will also inject any dependencies if specified
 * 
 * <p>
 * 
 * You'll still have to manually define REST API controller if needed, but at
 * least you'll have a Service ready in couple lines of code
 * 
 * <p>
 * 
 * You can use generic interface {@link EasyCrudService} or you can subclass it
 * with your own interface and have instance of typed interface. lAtter will
 * give you more clearer code.
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudScaffold {

	/**
	 * Build impl of the custom service interface
	 * 
	 * @param serviceInterface
	 *            custom service interface
	 * @param messageCode
	 *            entity message code
	 * @param tableName
	 *            name of the datble in the database
	 * @param injections
	 *            optional list of injections you want to make into service.
	 *            Scaffolder will automatically detect supported types and will
	 *            wrap it into wire taps if needed (or other way, depending on
	 *            impl)
	 * @return impl of the service
	 */
	<TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> TService fromService(
			Class<TService> serviceInterface, String messageCode, String tableName, Object... injections);

	/**
	 * Build impl of {@link EasyCrudService} based on the provided DTO class
	 * 
	 * <p>
	 * 
	 * Message code and Table name will be assumed based on DTO class name. I.e.
	 * if name of the DTO class is SomeDto then message code will be "SomeDto"
	 * and table name will be "some_dto".
	 * 
	 * You can use {@link #fromDto(Class, String, String, Object...)} in case
	 * you want to specify those manually. It also allows you to provide list of
	 * injections you want to do into service implementation.
	 * 
	 */
	<TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass);

	/**
	 * 
	 * 
	 * @param dtoClass
	 *            dto that reflects row in a database
	 * @param messageCode
	 *            message code used to identify service
	 * @param tableName
	 *            name of the table in the database
	 * @param injections
	 *            optional list of injections you want to make into service.
	 *            Scaffolder will automatically detect supported types and will
	 *            wrap it into wire taps if needed (or other way, depending on
	 *            impl)
	 * @return EasyCrudService ready for use
	 */
	<TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass, String messageCode,
			String tableName, Object... injections);

}
