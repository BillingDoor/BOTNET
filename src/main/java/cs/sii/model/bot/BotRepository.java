package cs.sii.model.bot;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotRepository extends CrudRepository<Bot, Integer> {

	List<Bot> findAll();

	/**
	 * @param idBot
	 * @return
	 */
	Bot findByIdBot(@Param("idBot") String idBot);

	/**
	 * @param mac
	 * @return
	 */
	List<Bot> findByMac(@Param("Mac") String mac);

	/**
	 * @param os
	 * @return
	 */
	List<Bot> findByOs(@Param("OS") String os);

	/**
	 * @param userName
	 * @return
	 */
	List<Bot> findByUsernameOS(@Param("UsernameOS") String userName);

	/**
	 * @param pkey
	 * @return
	 */
	List<Bot> findBypubKey(@Param("PubKey") String pkey);

	/**
	 * @param ip
	 * @return
	 */
	Bot findByip(@Param("Ip") String ip);

	/**
	 * @param id
	 * @return
	 */
	Bot findById(@Param("id") Integer id);

}