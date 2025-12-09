package com.digitalcoin.holdings_service.exception;

import com.digitalcoin.holdings_service.util.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理数据库连接失败异常
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleDatabaseConnectionException(DataAccessResourceFailureException e) {
        // 使用try-catch确保异常处理器本身不会抛出异常
        try {
            System.err.println("数据库连接失败异常: " + e.getMessage());
            // 避免完整的堆栈跟踪打印，减少潜在的性能问题
            
            // 返回友好的错误信息
            Result<String> result = Result.fail("数据库连接失败，请检查数据库服务是否正常运行");
            return new ResponseEntity<>(result, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception handlerException) {
            // 如果处理器本身出错，确保至少返回一个有效的响应
            System.err.println("异常处理器内部错误: " + handlerException.getMessage());
            Result<String> fallbackResult = Result.fail("服务器处理请求时发生错误");
            return new ResponseEntity<>(fallbackResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleSQLException(SQLException e) {
        System.err.println("SQL异常: " + e.getMessage());
        e.printStackTrace();
        
        // 返回友好的错误信息
        Result<String> result = Result.fail("数据库操作异常: " + e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理数据访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleDataAccessException(DataAccessException e) {
        System.err.println("数据访问异常: " + e.getMessage());
        e.printStackTrace();
        
        // 返回友好的错误信息
        Result<String> result = Result.fail("数据访问异常: " + e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleGeneralException(Exception e) {
        System.err.println("通用异常: " + e.getMessage());
        e.printStackTrace();
        
        // 返回友好的错误信息
        Result<String> result = Result.fail("服务器内部错误，请稍后重试");
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResponseEntity<Result<String>> handleBusinessException(BusinessException e) {
        System.out.println("业务异常: " + e.getMessage());
        
        // 返回业务异常信息
        Result<String> result = Result.fail(e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
}