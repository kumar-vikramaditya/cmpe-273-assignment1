package demo;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ModeratorController {

	private final AtomicLong counter = new AtomicLong();
	private final AtomicLong counter2 = new AtomicLong();
	private long moderatorId;
	private Moderator newModerator = null;
	private long pollId;
	private Poll newPoll = null;

	/**
	 * 
	 * moderator end points begin
	 * 
	 */
	TimeZone tz = TimeZone.getTimeZone("UTC");
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'T'");
	@RequestMapping(value = "/moderators", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createModerator(
			@RequestBody @Valid Moderator moderator) {

		moderatorId = counter.incrementAndGet();
		newModerator = new Moderator(moderatorId, moderator.getName(),
				moderator.getEmail(), moderator.getPassword(),
				df.format(new Date()));
		Moderator.moderatorsTable.put(moderatorId, newModerator);
		return new ResponseEntity<Object>(newModerator, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/moderators/{moderator_id}", method = RequestMethod.GET, headers={"accept=application/json"})
	public @ResponseBody ResponseEntity<Object> viewModerator(
			@PathVariable("moderator_id") long id,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {
		if (checkAuthorizationDetail(authorizationDetail)) {
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			for (long i : keySet) {
				if (i == id) {
					return new ResponseEntity<Object>(
							Moderator.moderatorsTable.get(i), HttpStatus.OK);
				}
			}

			return null;
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/moderators/{moderator_id}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> updateModerator(
			@PathVariable("moderator_id") long id,
			@RequestBody Moderator moderator,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {
		if (checkAuthorizationDetail(authorizationDetail)) {
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			Moderator tempMod = null;
			for (long i : keySet) {
				if (i == id) {
					tempMod = new Moderator();
					tempMod = Moderator.moderatorsTable.get(i);
					if (moderator.getEmail() != null) {
						tempMod.setEmail(moderator.getEmail());
					}
					if (moderator.getPassword() != null) {
						tempMod.setPassword(moderator.getPassword());
					}
					if (moderator.getName() != null) {
						tempMod.setName(moderator.getName());
					}
					return new ResponseEntity<Object>(tempMod,
							HttpStatus.CREATED);
				}
			}

			return new ResponseEntity<Object>(tempMod, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.BAD_REQUEST);
		}

	}

	/**
	 * 
	 * poll end points begin
	 * 
	 */

	@RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> createPoll(
			@PathVariable("moderator_id") long id,
			@RequestBody @Valid Poll poll,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {

		if (checkAuthorizationDetail(authorizationDetail)) {
			LinkedHashMap tempMap = null;
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			Moderator tempMod = new Moderator();
			ArrayList<Poll> tempPollList;
			for (long i : keySet) {
				if (i == id) {
					tempMod = Moderator.moderatorsTable.get(i);
					tempPollList = tempMod.getPollList();
					pollId = counter2.incrementAndGet();
					double myRnd = Math.random();
					pollId = (long) ((myRnd * 1234567) + pollId);
					newPoll = new Poll(Long.toHexString(pollId),
							poll.getQuestion(),
							poll.getStarted_at().toString(), poll
									.getExpired_at().toString(),
							poll.getChoice());

					int temp1 = poll.getChoice().length;
					int[] tempRes = newPoll.getResults();
					tempRes = new int[temp1];
					newPoll.setResults(tempRes);

					tempPollList.add(newPoll);
					Moderator.globalList.add(newPoll);

					tempMap = new LinkedHashMap();
					tempMap.put("id", newPoll.getId());
					tempMap.put("question", newPoll.getQuestion());
					tempMap.put("started_at", newPoll.getStarted_at());
					tempMap.put("expired_at", newPoll.getExpired_at());
					tempMap.put("choice", newPoll.getChoice());
					return new ResponseEntity<Object>(tempMap,
							HttpStatus.CREATED);
				}
			}
			return new ResponseEntity<Object>(tempMap, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity<LinkedHashMap> viewPoll(
			@PathVariable("poll_id") String poll_id) {
		LinkedHashMap tempMap = new LinkedHashMap();
		for (Poll poll : Moderator.globalList) {
			if (poll.getId().toString().equals(poll_id)) {

				tempMap.put("id", poll.getId());
				tempMap.put("question", poll.getQuestion());
				tempMap.put("started_at", poll.getStarted_at());
				tempMap.put("expired_at", poll.getExpired_at());
				tempMap.put("choice", poll.getChoice());

				return new ResponseEntity<LinkedHashMap>(tempMap, HttpStatus.OK);
			}
		}
		return null;
	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity<Object> viewPollWithResults(
			@PathVariable("poll_id") String poll_id,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {
		if (checkAuthorizationDetail(authorizationDetail)) {
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			Moderator tempMod = new Moderator();
			ArrayList<Poll> tempPollList;
			for (long i : keySet) {

				tempMod = Moderator.moderatorsTable.get(i);
				tempPollList = tempMod.getPollList();
				for (Poll poll : tempPollList) {
					if (poll.getId().toString().equals(poll_id)) {
						return new ResponseEntity<Object>(poll, HttpStatus.OK);
					}
				}

			}
			return null;
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.NO_CONTENT);
		}

	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.GET)
	public ResponseEntity<Object> viewAllPolls(
			@PathVariable("moderator_id") long moderator_id,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {
		if (checkAuthorizationDetail(authorizationDetail)) {
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			Moderator tempMod = new Moderator();
			ArrayList<Poll> tempPollList;
			for (long i : keySet) {

				if (i == moderator_id) {
					tempMod = Moderator.moderatorsTable.get(i);
					return new ResponseEntity<Object>(tempMod.getPollList(),
							HttpStatus.OK);
				}

			}
			return null;
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.NO_CONTENT);
		}

	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePoll(
			@PathVariable("moderator_id") long moderator_id,
			@PathVariable("poll_id") String poll_id,
			@RequestHeader(value = "Authorization") String authorizationDetail)
			throws UnsupportedEncodingException {
		if (checkAuthorizationDetail(authorizationDetail)) {
			Set<Long> keySet = Moderator.moderatorsTable.keySet();
			Moderator tempMod = new Moderator();
			ArrayList<Poll> tempPollList;
			for (long i : keySet) {

				if (i == moderator_id) {
					tempMod = Moderator.moderatorsTable.get(i);
					tempPollList = tempMod.getPollList();
					for (Poll poll : tempPollList) {
						if (poll.getId().toString().equals(poll_id)) {
							Moderator.globalList.remove(poll);
							tempPollList.remove(poll);
							return new ResponseEntity(HttpStatus.NO_CONTENT);
						}
					}

				}

			}
			 return null;
		} else {
			return new ResponseEntity<Object>("Invalid Username and Password",
					HttpStatus.NO_CONTENT);
		}

	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.PUT)
	public ResponseEntity registerVote(@PathVariable("poll_id") String poll_id,
			@RequestParam Integer choice) {
		for (Poll poll : Moderator.globalList) {

			if (poll.getId().toString().equals(poll_id)) {

				poll.getResults()[choice]++;
				return new ResponseEntity(HttpStatus.NO_CONTENT);
			}

		}
		return null;
	}

	public boolean checkAuthorizationDetail(String authorizationDetail)
			throws UnsupportedEncodingException {
		String[] authorizationDetailArray = authorizationDetail.split(" ");
		byte[] decodedString = Base64.decodeBase64(authorizationDetailArray[1]);
		String authorizationString = new String(decodedString, "UTF-8");
		if (authorizationString.indexOf(":") > 0) {
			String[] credentials = authorizationString.split(":");
			String username = credentials[0];
			String password = credentials[1];
			if (username.equals("foo") && password.equals("bar"))
				return true;
		}
		return false;

	}

}
