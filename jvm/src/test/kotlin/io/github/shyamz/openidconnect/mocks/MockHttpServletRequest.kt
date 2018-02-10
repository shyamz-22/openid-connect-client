package io.github.shyamz.openidconnect.mocks

import java.io.BufferedReader
import java.security.Principal
import java.util.*
import javax.servlet.*
import javax.servlet.http.*

class MockHttpServletRequest(private val requestUri: String,
                             private val queryParams: Map<String, Array<String>>) : HttpServletRequest {

    override fun isUserInRole(role: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startAsync(): AsyncContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startAsync(servletRequest: ServletRequest?, servletResponse: ServletResponse?): AsyncContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPathInfo(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProtocol(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCookies(): Array<Cookie> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        return queryParams
    }

    override fun getRequestURL(): StringBuffer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttributeNames(): Enumeration<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCharacterEncoding(env: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParameterValues(name: String?): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemoteAddr(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAsyncStarted(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentLengthLong(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocales(): Enumeration<Locale> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRealPath(path: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun login(username: String?, password: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContextPath(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRequestedSessionIdValid(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServerPort(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAttribute(name: String?): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDateHeader(name: String?): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemoteHost(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRequestedSessionId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServletPath(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSession(create: Boolean): HttpSession {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSession(): HttpSession {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServerName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalAddr(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSecure(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : HttpUpgradeHandler?> upgrade(handlerClass: Class<T>?): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRequestedSessionIdFromCookie(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPart(name: String?): Part {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRemoteUser(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocale(): Locale {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMethod(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRequestedSessionIdFromURL(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalPort(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRequestedSessionIdFromUrl(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getServletContext(): ServletContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getQueryString(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDispatcherType(): DispatcherType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeaders(name: String?): Enumeration<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUserPrincipal(): Principal {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParts(): MutableCollection<Part> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReader(): BufferedReader {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getScheme(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logout() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getInputStream(): ServletInputStream {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLocalName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAsyncSupported(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAuthType(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCharacterEncoding(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParameterNames(): Enumeration<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authenticate(response: HttpServletResponse?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAttribute(name: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPathTranslated(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentLength(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeader(name: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIntHeader(name: String?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeSessionId(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentType(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAsyncContext(): AsyncContext {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRequestURI(): String {
        return requestUri
    }

    override fun getRequestDispatcher(path: String?): RequestDispatcher {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getHeaderNames(): Enumeration<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAttribute(name: String?, o: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParameter(name: String?): String? {
        return queryParams[name]?.first()
    }

    override fun getRemotePort(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}