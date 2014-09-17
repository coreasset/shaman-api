package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import itwise.coreasset.shaman.api.model.ObjectList;

import java.util.HashMap;
import java.util.Map.Entry;

import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * API 테스트를 위한 Util
 * 
 * @author kkuru
 *
 * @param <T>
 */
public class UnitTest4RestAPIClient<T> {
	
	private String defaultURI;
	private Class<T> defaultClass;
	private Boolean isPrint = false;
	private final MockMvc mockMvc;
	private final ObjectMapper mapper = new ObjectMapper();;
	
	public UnitTest4RestAPIClient(WebApplicationContext context) {
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
//		Thread.java > UnitTest4RestAPIClient > Caller Class
		MockitoAnnotations.initMocks(stacktrace[2]);
		
		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}
	
	public T requestCreate(T obj, ResultMatcher... matchers) throws Exception {
		String requestMessage = mapper.writeValueAsString(obj);
		
		ResultActions result = this.mockMvc.perform(post(defaultURI)
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
//		check expect
		if(matchers.length == 0){
			result.andExpect(status().isCreated());
			result.andExpect(jsonPath("$.idx", greaterThan(0)));
		} else {
			for (ResultMatcher resultMatcher : matchers) {
				result.andExpect(resultMatcher);
			}
		}
		
		if(isPrint){
			result.andDo(print());
		}
		
		try {
			return convert(result.andReturn());
		} catch (Exception e) {
			return null;
		}
	}

	public T requestUpdate(T obj, String resourceId, ResultMatcher... matchers) throws Exception {
		String requestMessage = mapper.writeValueAsString(obj);
		
		
		MockHttpServletRequestBuilder requestBuilder = put(defaultURI + "/" + resourceId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(requestMessage);
			
		ResultActions result = this.mockMvc.perform(requestBuilder);
		
//		check expect
		if(matchers.length == 0){
			result.andExpect(status().isCreated());
		} else {
			for (ResultMatcher resultMatcher : matchers) {
				result.andExpect(resultMatcher);
			}
		}
		
		if(isPrint){
			result.andDo(print());
		}
		
		return convert(result.andReturn());
	}

	/**
	 * Request Delete, No Parameters
	 * 
	 * @param resourceId
	 * @param matchers
	 * @return
	 * @throws Exception
	 */
	public T requestDelete(String resourceId, ResultMatcher... matchers) throws Exception {
		return requestDelete(resourceId, new HashMap<String, String>(), matchers);
	}
	
	/**
	 * Request Delete with parameters
	 * 
	 * @param resourceId
	 * @param params
	 * @param matchers
	 * @return
	 * @throws Exception
	 */
	public T requestDelete(String resourceId, HashMap<String, String> params, ResultMatcher... matchers) throws Exception {

		MockHttpServletRequestBuilder requestBuilder = delete(defaultURI + "/" + resourceId)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		
		for (Entry<String, String> element : params.entrySet()){
			requestBuilder.param(element.getKey(), element.getValue());
		}
		
		ResultActions result = this.mockMvc.perform(requestBuilder);
		
//		check expect
		if(matchers.length == 0){
			result.andExpect(status().isAccepted());
		} else {
			for (ResultMatcher resultMatcher : matchers) {
				result.andExpect(resultMatcher);
			}
		}
		
		if(isPrint){
			result.andDo(print());
		}
			
		return convert(result.andReturn());
	}
	
	public T requestGet(ResultMatcher... matchers) throws Exception {
		
		
		MockHttpServletRequestBuilder requestBuilder = get(defaultURI)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		
//		for (Entry<String, String> element : params.entrySet()){
//			requestBuilder.param(element.getKey(), element.getValue());
//		}
		
		ResultActions result = this.mockMvc.perform(requestBuilder);
		
//		check expect
		if(matchers.length == 0){
			result.andExpect(status().isOk());
		} else {
			for (ResultMatcher resultMatcher : matchers) {
				result.andExpect(resultMatcher);
			}
		}
		
		if(isPrint){
			result.andDo(print());
		}
			
		
		return convert(result.andReturn());
	}

	public ObjectList<T> requestGetList(ResultMatcher... matchers) throws Exception {
		HashMap<String, String> params = new HashMap<String, String>();
		return requestGetList(params, matchers);
	}


	public ObjectList<T> requestGetList(HashMap<String, String> params, ResultMatcher... matchers) throws Exception{

		MockHttpServletRequestBuilder requestBuilder = get(defaultURI)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		
		for (Entry<String, String> element : params.entrySet()){
			requestBuilder.param(element.getKey(), element.getValue());
		}
		
		ResultActions result = this.mockMvc.perform(requestBuilder);

//		check expect
		if(matchers.length == 0){
			result.andExpect(status().is2xxSuccessful());
		} else {
			for (ResultMatcher resultMatcher : matchers) {
				result.andExpect(resultMatcher);
			}
		}
		
		if(isPrint){
			result.andDo(print());
		}
		
		return convertList(result.andReturn());
	}
	

	public T convert(MvcResult result) throws Exception{
		String responseBody = result.getResponse().getContentAsString();
		
		if(result.getResponse().getStatus() == HttpStatus.NO_CONTENT.value() || responseBody.isEmpty()) {
			return (T) defaultClass;
		} else {
			return mapper.readValue(responseBody,  defaultClass);
		}
	}
	
	public ObjectList<T> convertList(MvcResult result) throws Exception{
		String responseBody = result.getResponse().getContentAsString();
		
		if(result.getResponse().getStatus() == HttpStatus.NO_CONTENT.value() || responseBody.isEmpty()) {
			return new ObjectList<T>();
		} else {
			return mapper.readValue(responseBody,  ObjectList.class);
		}
	}
	
	/**
	 * @param isPrint the isPrint to set
	 */
	public void setIsPrint(Boolean isPrint) {
		this.isPrint = isPrint;
	}

	/**
	 * @return the defaultURI
	 */
	public String getDefaultURI() {
		return defaultURI;
	}

	/**
	 * @param defaultURI the defaultURI to set
	 */
	public void setDefaultURI(String defaultURI) {
		this.defaultURI = defaultURI;
	}

	/**
	 * @return the defaultClass
	 */
	public Class<T> getDefaultClass() {
		return defaultClass;
	}

	/**
	 * @param defaultClass the defaultClass to set
	 */
	public void setDefaultClass(Class<T> defaultClass) {
		this.defaultClass = defaultClass;
	}
}
