package cs.sii.model.bot;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

//@RepositoryRestResource(collectionResourceRel = "botter", path = "botter")
public interface BotRepository extends CrudRepository<Bot, Integer> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
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
	Bot findById(@Param("id")Integer id);

	//
	// List<Bot> findBychainLevelGreaterThan(@Param("cLevel") Integer cLevel);
	//
	// List<Bot>
	// findByChainLevelGreaterThanOrderByChainLevelDesc(@Param("cLevel") Integer
	// cLevel);
	//
	// List<Bot>
	// findTop10ByChainLevelGreaterThanOrderByChainLevelDesc(@Param("cLevel")
	// Integer cLevel);
	//
	// List<Bot> findByChainLevelBetweenOrderByChainLevelAsc(@Param("inf")
	// Integer inf,@Param("sup") Integer sup);

}