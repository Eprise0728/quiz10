package com.example.quiz10.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz10.constants.ResMessage;
import com.example.quiz10.constants.SelectType;
import com.example.quiz10.entity.Feedback;
import com.example.quiz10.entity.Ques;
import com.example.quiz10.entity.Quiz;
import com.example.quiz10.repositort.FeedbackDao;
import com.example.quiz10.repositort.QuesDao;
import com.example.quiz10.repositort.QuizDao;
import com.example.quiz10.service.ifs.QuizService;
import com.example.quiz10.vo.BasicRes;
import com.example.quiz10.vo.CreateUpdateReq;
import com.example.quiz10.vo.DeleteReq;
import com.example.quiz10.vo.FeedbackRes;
import com.example.quiz10.vo.FillinReq;
import com.example.quiz10.vo.QuizRes;
import com.example.quiz10.vo.SearchReq;
import com.example.quiz10.vo.SearchRes;
import com.example.quiz10.vo.StatisticsRes;
import com.example.quiz10.vo.StatisticsVo;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuesDao quesDao;

	@Autowired
	private FeedbackDao feedbackDao;

	@Transactional
	@Override
	public BasicRes create(CreateUpdateReq req) {
		// 基本的屬性判斷已交由@Valide
		// 開始時間不能比結束時間晚
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}
		// 判斷問題型態非文字時，選項要有值
		List<Ques> quesList = req.getQuesList();
		for (Ques item : quesList) {
//			if (item.getType().equalsIgnoreCase(SelectType.TEXT.getType())) {
//			}
			// 不使用上面的寫法是因為從req過來的參數值可會有第4種的值
			// 下面的判斷式的寫法就必須視SINGLE或MULTI其中之一
			if (item.getType().equalsIgnoreCase(SelectType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(SelectType.MULTI.getType())) {
				// 確定是單或多選，選項就必須要有值
				// 前面有驚嘆號表示否定的意思
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		// 因為Quiz中的id是AI自動生成的流水號，要讓quizDao執行save後可以把該id的值回傳，
		// 必須要由Quiz此Entity將資料型態為int屬性id
		// 加上@GeneratedValue(strategy = GenerationType.IDENTITY)
		Quiz res = quizDao.save(
				new Quiz(req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), req.isPublished()));
		// 將返回的res中的id(quiz_id)塞到Ques中的quizId此屬性上
		quesList.forEach(item -> {
			item.setQuizId(res.getId());
		});
		quesDao.saveAll(quesList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {
		// 基本的屬性判斷已交由@Valide
		// 開始時間不能比結束時間晚
		if (req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATA_ERROR.getCode(), ResMessage.DATA_ERROR.getMessage());
		}
		// 判斷問題型態非文字時，選項要有值
		List<Ques> quesList = req.getQuesList();
		for (Ques item : quesList) {
			// 檢查問題中的quizId和問卷中的id是否相同
			if (item.getQuizId() != req.getId()) {
				return new BasicRes(ResMessage.QUIZ_ID_NOT_MATCH.getCode(), ResMessage.QUIZ_ID_NOT_MATCH.getMessage());
			}
//					if (item.getType().equalsIgnoreCase(SelectType.TEXT.getType())) {
//					}
			// 不使用上面的寫法是因為從req過來的參數值可會有第4種的值
			// 下面的判斷式的寫法就必須視SINGLE或MULTI其中之一
			if (item.getType().equalsIgnoreCase(SelectType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(SelectType.MULTI.getType())) {
				// 確定是單或多選，選項就必須要有值
				// 前面有驚嘆號表示否定的意思
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMessage.OPTIONS_ERROR.getCode(), ResMessage.OPTIONS_ERROR.getMessage());
				}
			}
		}
		// 檢查要修改的問卷是否已存在
		if (!quizDao.existsById(req.getId())) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		quizDao.save(new Quiz(req.getId(), req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(),
				req.isPublished()));
		// 刪除此張問卷所有的問題
		quesDao.deleteByQuizId(req.getId());
		// 新增更新後的問題
		quesDao.saveAll(req.getQuesList());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public BasicRes delete(DeleteReq req) {
		// 進行中的問卷不能刪除:找出要刪除的idList中是否有包含進行中的問卷
		// 進行中的問卷條件:1.已發佈 2.當前時間大於等於開始日期 3.當前時間小於等於結束日期
		boolean res = quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
				req.getQuizIdList(), LocalDate.now(), LocalDate.now());
		if (res) {// 等同於res == true 表示要刪除的問卷ID中有正在進行中的
			return new BasicRes(ResMessage.QUIZ_IN_PROGRESS.getCode(), ResMessage.QUIZ_IN_PROGRESS.getMessage());
		}
		quizDao.deleteAllById(req.getQuizIdList());
		return null;
	}

	@Override
	public SearchRes search(SearchReq req) {
		String quizName = req.getQuizName();
		LocalDate startDate = req.getStartDate();
		LocalDate endDate = req.getEndDate();
		if (!StringUtils.hasText(quizName)) {
			quizName = "";
		}
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);
		}
		if (endDate == null) {
			endDate = LocalDate.of(2999, 12, 31);
		}
		List<Quiz> res = quizDao.findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(quizName,
				startDate, endDate);
		List<QuizRes> quizResList = new ArrayList<>();
		// 方法1 使用foreach根據每個quizId去撈對應的Ques
		for (Quiz item : res) {
			// 根據quizId撈取每張問卷裡的quesList
			int quizId = item.getId();
			List<Ques> quesList = quesDao.findByQuizId(quizId);
			// 建立QuizRes用來放Quiz和對應的List<Ques>
			// 若參數太多使用建構方法容易混亂，建議用以下的方法
			QuizRes quizRes = new QuizRes();
			quizRes.setId(quizId);
			quizRes.setName(item.getName());
			quizRes.setDescription(item.getDescription());
			quizRes.setStartDate(item.getStartDate());
			quizRes.setEndDate(item.getEndDate());
			quizRes.setPublished(item.isPublished());
			quizRes.setQuesList(quesList);
			// 把每張不同的問卷+問題放進List<QuizRes>中
			quizResList.add(quizRes);
		}
		// 方法2:先蒐集所有符合的quizId 在一次撈取符合條件的Ques:不管問卷多寡，固定就是連接DB 2次
		List<Integer> quizList = new ArrayList<>();
		for (Quiz item : res) {
			quizList.add(item.getId());
		}
		List<Ques> quesList = quesDao.findByQuizIdIn(quizList);
		// 將res(所有的問卷)和quesList(所有的問題)配對
		List<QuizRes> quizResList2 = new ArrayList<>();
		for (Quiz item : res) {
			int quizId = item.getId();
			List<Ques> returnQuesList = new ArrayList<>();
			for (Ques quesItem : quesList) {
				// 判斷Quiz和Ques中的quizId是否有相同
				if (quizId == quesItem.getId()) {
					// quizId一樣就加進要返回的QuesList中
					returnQuesList.add(quesItem);
				}
			}
			QuizRes quizRes = new QuizRes();
			quizRes.setId(quizId);
			quizRes.setName(item.getName());
			quizRes.setDescription(item.getDescription());
			quizRes.setStartDate(item.getStartDate());
			quizRes.setEndDate(item.getEndDate());
			quizRes.setPublished(item.isPublished());
			quizRes.setQuesList(returnQuesList);
			// 把每張不同的問卷+問題放進List<QuizRes>中
			quizResList2.add(quizRes);
		}
		// return new SearchRes(ResMessage.SUCCESS.getCode(),
		// ResMessage.SUCCESS.getMessage(),quizResList2);
		return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quizResList);
	}

	@Override
	public BasicRes fillin(FillinReq req) {
		// 先檢查req中的List<Feedback>，所有的Feedback的quizId和email都是一樣的
		// 就是quizId和email只會有一個
		List<Feedback> feedbackList = req.getFeedbackList();
		Set<Integer> quizIdSet = new HashSet<>();
		Set<String> emailSet = new HashSet<>();
		for (Feedback item : feedbackList) {
			quizIdSet.add(item.getQuizId());
			emailSet.add(item.getEmail());
		}
		// 因為set的值不會有重複 ，所以兩個set的大小都不是1的話就表示quizId或email有填錯
		if (quizIdSet.size() != 1 || emailSet.size() != 1) {
			return new BasicRes(ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getCode(),
					ResMessage.QUIZ_ID_OR_EMAIL_INCONSISTENT.getMessage());
		}
		int quizId = req.getFeedbackList().get(0).getQuizId();
		// 檢查同一個email + quizId是否已存在(同一個email已經填寫過同一張問卷)
		if (feedbackDao.existsByQuizIdAndEmail(quizId, req.getFeedbackList().get(0).getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATE.getCode(), ResMessage.EMAIL_DUPLICATE.getMessage());
		}
		// 檢查問卷是否可以填寫的狀態:1.已發佈 2.當前時間大於等於開始日期 3.當前時間小於等於結束日期
		// 前面有驚嘆號表示找不到已發佈且當前時間是介於開始時間與結束時間之間的資料
		if (!quizDao.existsByIdInAndPublishedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(List.of(quizId),
				LocalDate.now(), LocalDate.now())) {
			return new BasicRes(ResMessage.CANNOT_FILLIN_QUIZ.getCode(), ResMessage.CANNOT_FILLIN_QUIZ.getMessage());
		} // 檢查答案
		List<Ques> quesList = quesDao.findByQuizId(quizId);
		// 檢查問題和答案的筆數是否一樣
		if (feedbackList.size() != quesList.size()) {
			return new BasicRes(ResMessage.FILLIN_INCOMPLETE.getCode(), ResMessage.FILLIN_INCOMPLETE.getMessage());
		}
		Set<Integer> necessaryQuIds = new HashSet<>();
		Set<Integer> singleQuIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Ques quesItem : quesList) {
			quIds.add(quesItem.getId());
			if (quesItem.isNecessary()) {
				necessaryQuIds.add(quesItem.getId());
			}
			if (quesItem.getType().equalsIgnoreCase(SelectType.SINGLE.getType())) {
				singleQuIds.add(quesItem.getId());
			}
		}
		Map<Integer, List<String>> quIdAnsMap = new HashMap<>();
		for (Feedback item : feedbackList) {
			int quId = item.getQuId();
			if (!quIds.contains(quId)) {
				return new BasicRes(ResMessage.QUID_MISMATCH.getCode(), ResMessage.QUID_MISMATCH.getMessage());
			}
			if (necessaryQuIds.contains(quId) && !StringUtils.hasText(item.getAns())) {
				return new BasicRes(ResMessage.FILLIN_IS_NECESSARY.getCode(),
						ResMessage.FILLIN_IS_NECESSARY.getMessage());
			}
			List<String> ansList = List.of(item.getAns().split(";"));
			if (singleQuIds.contains(quId) && ansList.size() > 1) {
				return new BasicRes(ResMessage.SINGLE_CHOICE_QUES.getCode(),
						ResMessage.SINGLE_CHOICE_QUES.getMessage());
			}
			quIdAnsMap.put(quId, ansList);
		}
		for (Ques item : quesList) {
			String type = item.getType();
			if (type.equalsIgnoreCase(SelectType.TEXT.getType())) {
				List<String> ansList = quIdAnsMap.get(item.getId());
				List<String> optionList = List.of(item.getOptions().split(";"));
				if (!optionList.containsAll(ansList) && ansList.isEmpty()) {
					return new BasicRes(ResMessage.OPTION_ANSWER_MISMATCH.getCode(),
							ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
				}
			}
		}
		feedbackDao.saveAll(feedbackList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		// 先撈取quiz的相關資訊
		Optional<Quiz> op = quizDao.findById(quizId);
		if (op.isEmpty()) {
			return new StatisticsRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		String quizName = quiz.getName();
		// 先從ques撈取非文字類型的問題(文字類型的問題不列入統計)
		List<Ques> quesList = quesDao.findByQuizIdAndTypeNot(quizId, SelectType.TEXT.getType());
		List<Integer> quIdList = new ArrayList<>();
		Map<Integer, Map<String, Integer>> quIdOptionCountMap = new HashMap<>();
		List<StatisticsVo> statisticsList = new ArrayList<>();
		for (Ques item : quesList) {
			quIdList.add(item.getId());
			Map<String, Integer> optionCountMap = new HashMap<>();
			List<String> optionList = List.of(item.getOptions().split(";"));
			for (String option : optionList) {
				optionCountMap.put(option, 0);
			}
			quIdOptionCountMap.put(item.getId(), optionCountMap);
			StatisticsVo vo = new StatisticsVo();
			vo.setQuId(item.getId());
			vo.setQu(item.getOptions());
			statisticsList.add(vo);
		}
		List<Feedback> feedbackList = feedbackDao.findByQuizIdAndQuIdIn(quizId, quIdList);

		for (Feedback item : feedbackList) {
	        Map<String, Integer> optionCountMap = quIdOptionCountMap.get(item.getQuId());
	        if (optionCountMap != null) {  // 檢查 optionCountMap 是否為 null
	            List<String> ansList = List.of(item.getAns().split(";"));
	            for (String ans : ansList) {
	                if (optionCountMap.containsKey(ans)) {  // 檢查 ans 是否存在於 optionCountMap 中
	                    int count = optionCountMap.get(ans);
	                    count++;
	                    optionCountMap.put(ans, count);
	                } 
	            }
	            quIdOptionCountMap.put(item.getQuId(), optionCountMap);
	        } 
	    }
		for (StatisticsVo item : statisticsList) {
			int quId = item.getQuId();
			Map<String, Integer> optionCountMap = quIdOptionCountMap.get(quId);
			item.setOptionCountMap(optionCountMap);
			// 上面3行程式碼可用下面一行表示
			// item.setOptionCountMap(quIdOptionCountMap.get(item.getQuId());
		}
		return new StatisticsRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quizName,
				statisticsList);
	}

	@Override
	public FeedbackRes feedback(int quizId) {

		if (quizId <= 0) {
			return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), new ArrayList<>());
		}
		List<Feedback> res = feedbackDao.findByQuizId(quizId);
		return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), res);
		
		//三元式寫法
//		return quizId <= 0 ? new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
//				new ArrayList<>())
//				: new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), //
//						feedbackDao.findByQuizId(quizId));
	}
}
