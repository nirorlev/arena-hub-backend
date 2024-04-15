--
-- PostgreSQL database dump
--

-- Dumped from database version 16.2 (Debian 16.2-1.pgdg120+2)
-- Dumped by pg_dump version 16.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: on_update_current_timestamp_gc_course_cate(); Type: FUNCTION; Schema: arena_hub; Owner: postgres
--

CREATE FUNCTION arena_hub.on_update_current_timestamp_gc_course_cate() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
   NEW.update_time = now();
   NEW.create_time = now();
   RETURN NEW;
END;
$$;


ALTER FUNCTION arena_hub.on_update_current_timestamp_gc_course_cate() OWNER TO postgres;

--
-- Name: on_update_current_timestamp_gc_subject_complete(); Type: FUNCTION; Schema: arena_hub; Owner: postgres
--

CREATE FUNCTION arena_hub.on_update_current_timestamp_gc_subject_complete() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
   NEW.update_time = now();
   NEW.create_time = now();
   RETURN NEW;
END;
$$;


ALTER FUNCTION arena_hub.on_update_current_timestamp_gc_subject_complete() OWNER TO postgres;

--
-- Name: on_update_current_timestamp_sys_menu(); Type: FUNCTION; Schema: arena_hub; Owner: postgres
--

CREATE FUNCTION arena_hub.on_update_current_timestamp_sys_menu() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
   NEW.update_time = now();
   NEW.create_time = now();
   RETURN NEW;
END;
$$;


ALTER FUNCTION arena_hub.on_update_current_timestamp_sys_menu() OWNER TO postgres;

--
-- Name: on_update_current_timestamp_sys_role_menu(); Type: FUNCTION; Schema: arena_hub; Owner: postgres
--

CREATE FUNCTION arena_hub.on_update_current_timestamp_sys_role_menu() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
   NEW.update_time = now();
   RETURN NEW;
END;
$$;


ALTER FUNCTION arena_hub.on_update_current_timestamp_sys_role_menu() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: feature_toggle; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.feature_toggle (
    id integer NOT NULL,
    name text NOT NULL,
    description text,
    value text NOT NULL,
    created_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.feature_toggle OWNER TO postgres;

--
-- Name: TABLE feature_toggle; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.feature_toggle IS 'Table to handle feature toggle configuration for application';


--
-- Name: feature_toggle_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.feature_toggle_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.feature_toggle_id_seq OWNER TO postgres;

--
-- Name: feature_toggle_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.feature_toggle_id_seq OWNED BY arena_hub.feature_toggle.id;


--
-- Name: gc_access; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_access (
    id integer NOT NULL,
    master_id integer NOT NULL,
    role_type boolean,
    admin_id integer,
    code text NOT NULL,
    package_learning_hours double precision,
    subject_json json NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    code_type integer DEFAULT 0,
    free_flag integer,
    package_name text,
    package_price_period json,
    package_description text,
    package_additional_course_information text,
    package_img_id integer,
    package_show_flag integer,
    package_video_file_id integer,
    "order" integer,
    try_for_free_link text,
    try_for_free_text text,
    must_subject_json json,
    may_subject_json json,
    channel_json json,
    group_name text,
    subscribe_json json,
    role_json json,
    super_teacher integer
);


ALTER TABLE arena_hub.gc_access OWNER TO postgres;

--
-- Name: COLUMN gc_access.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.id IS '主键';


--
-- Name: COLUMN gc_access.role_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.role_type IS '角色类型';


--
-- Name: COLUMN gc_access.admin_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.admin_id IS 'gc_access的admin_id';


--
-- Name: COLUMN gc_access.code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.code IS 'code';


--
-- Name: COLUMN gc_access.subject_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.subject_json IS 'json';


--
-- Name: COLUMN gc_access.code_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.code_type IS '0为用户注册码、1为门户注册码';


--
-- Name: COLUMN gc_access.free_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.free_flag IS '1为免费，0为付费';


--
-- Name: COLUMN gc_access.package_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.package_name IS '套餐名';


--
-- Name: COLUMN gc_access.package_price_period; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.package_price_period IS '价格以及价格period';


--
-- Name: COLUMN gc_access.package_description; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.package_description IS '套餐描述';


--
-- Name: COLUMN gc_access.package_additional_course_information; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.package_additional_course_information IS '套餐额外课程信息';


--
-- Name: COLUMN gc_access.package_img_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.package_img_id IS '套餐封面文件id';


--
-- Name: COLUMN gc_access."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access."order" IS '支付包排序';


--
-- Name: COLUMN gc_access.must_subject_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.must_subject_json IS '必须学习的课程';


--
-- Name: COLUMN gc_access.may_subject_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.may_subject_json IS '有权限的课程';


--
-- Name: COLUMN gc_access.channel_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.channel_json IS 'channelJson';


--
-- Name: COLUMN gc_access.group_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.group_name IS '组名称';


--
-- Name: COLUMN gc_access.role_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.role_json IS '角色列表';


--
-- Name: COLUMN gc_access.super_teacher; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_access.super_teacher IS '超级老师0是 1否';


--
-- Name: gc_access_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_access_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_access_id_seq OWNER TO postgres;

--
-- Name: gc_access_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_access_id_seq OWNED BY arena_hub.gc_access.id;


--
-- Name: gc_category; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_category (
    id integer NOT NULL,
    category_name text,
    pid integer,
    level integer,
    update_time timestamp with time zone,
    create_time timestamp with time zone,
    "order" integer,
    file_name text
);


ALTER TABLE arena_hub.gc_category OWNER TO postgres;

--
-- Name: gc_chat_history; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_chat_history (
    id integer NOT NULL,
    history_id text,
    list_chat_id text,
    type integer,
    role integer,
    name text,
    text text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_chat_history OWNER TO postgres;

--
-- Name: TABLE gc_chat_history; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_chat_history IS '聊天记录';


--
-- Name: COLUMN gc_chat_history.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.id IS '主键';


--
-- Name: COLUMN gc_chat_history.history_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.history_id IS '前段传的history_id';


--
-- Name: COLUMN gc_chat_history.list_chat_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.list_chat_id IS 'chat_list关联id';


--
-- Name: COLUMN gc_chat_history.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.type IS '类型，文本=0，问题
=1，学习计划=2';


--
-- Name: COLUMN gc_chat_history.role; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.role IS '角色，系统消息System = 0；用户消息User = 1；gpt消息Assistant = 2';


--
-- Name: COLUMN gc_chat_history.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.name IS '名称';


--
-- Name: COLUMN gc_chat_history.text; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.text IS '文字，type=2时';


--
-- Name: COLUMN gc_chat_history.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.update_time IS '修改时间';


--
-- Name: COLUMN gc_chat_history.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_history.create_time IS '创建时间';


--
-- Name: gc_chat_history_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_chat_history_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_chat_history_id_seq OWNER TO postgres;

--
-- Name: gc_chat_history_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_chat_history_id_seq OWNED BY arena_hub.gc_chat_history.id;


--
-- Name: gc_chat_list; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_chat_list (
    id integer NOT NULL,
    chat_id text,
    text text,
    video_id integer,
    master_id integer,
    user_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    user_role smallint
);


ALTER TABLE arena_hub.gc_chat_list OWNER TO postgres;

--
-- Name: TABLE gc_chat_list; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_chat_list IS '聊天列表';


--
-- Name: COLUMN gc_chat_list.chat_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_list.chat_id IS 'chatid';


--
-- Name: COLUMN gc_chat_list.text; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_list.text IS '文字，type=2时';


--
-- Name: COLUMN gc_chat_list.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_list.update_time IS '修改时间';


--
-- Name: COLUMN gc_chat_list.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_list.create_time IS '创建时间';


--
-- Name: COLUMN gc_chat_list.user_role; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_chat_list.user_role IS '1门户，2 or null用户';


--
-- Name: gc_chat_list_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_chat_list_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_chat_list_id_seq OWNER TO postgres;

--
-- Name: gc_chat_list_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_chat_list_id_seq OWNED BY arena_hub.gc_chat_list.id;


--
-- Name: gc_content_group_course_assignment; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_content_group_course_assignment (
    id integer NOT NULL,
    course_id integer NOT NULL,
    created_by_user_id integer,
    content_group_id integer NOT NULL,
    is_mandatory smallint DEFAULT '0'::smallint NOT NULL,
    deadline timestamp with time zone,
    created_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_date timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_content_group_course_assignment OWNER TO postgres;

--
-- Name: TABLE gc_content_group_course_assignment; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_content_group_course_assignment IS 'Table is responsible for establishing relation between content group, course assigned to it and user who made the assignment';


--
-- Name: gc_content_group_course_assignment_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_content_group_course_assignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_content_group_course_assignment_id_seq OWNER TO postgres;

--
-- Name: gc_content_group_course_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_content_group_course_assignment_id_seq OWNED BY arena_hub.gc_content_group_course_assignment.id;


--
-- Name: gc_course_cate; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_course_cate (
    id integer NOT NULL,
    parent_id integer,
    type_name text,
    create_time timestamp with time zone,
    update_time timestamp with time zone
);


ALTER TABLE arena_hub.gc_course_cate OWNER TO postgres;

--
-- Name: COLUMN gc_course_cate.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_cate.id IS '主键';


--
-- Name: COLUMN gc_course_cate.parent_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_cate.parent_id IS '父级ID';


--
-- Name: COLUMN gc_course_cate.type_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_cate.type_name IS '类型名称';


--
-- Name: COLUMN gc_course_cate.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_cate.create_time IS '创建时间';


--
-- Name: COLUMN gc_course_cate.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_cate.update_time IS '更新时间';


--
-- Name: gc_course_cate_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_course_cate_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_course_cate_id_seq OWNER TO postgres;

--
-- Name: gc_course_cate_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_course_cate_id_seq OWNED BY arena_hub.gc_course_cate.id;


--
-- Name: gc_course_complete_read; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_course_complete_read (
    id integer NOT NULL,
    user_id integer,
    subject_id integer,
    target_user_id integer,
    is_read smallint,
    type smallint,
    master_id integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.gc_course_complete_read OWNER TO postgres;

--
-- Name: TABLE gc_course_complete_read; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_course_complete_read IS '信息已读表';


--
-- Name: COLUMN gc_course_complete_read.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.id IS '主键';


--
-- Name: COLUMN gc_course_complete_read.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.user_id IS '用户id';


--
-- Name: COLUMN gc_course_complete_read.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.subject_id IS '课程id';


--
-- Name: COLUMN gc_course_complete_read.target_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.target_user_id IS '当前老师id';


--
-- Name: COLUMN gc_course_complete_read.is_read; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.is_read IS '是否已读1代表已读0代表未读';


--
-- Name: COLUMN gc_course_complete_read.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.type IS '类型1代表WorkbooksCompletion2代表CompletedCourses';


--
-- Name: COLUMN gc_course_complete_read.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.master_id IS 'master的id';


--
-- Name: COLUMN gc_course_complete_read.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.create_time IS '创建时间';


--
-- Name: COLUMN gc_course_complete_read.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_course_complete_read.update_time IS '修改时间';


--
-- Name: gc_course_complete_read_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_course_complete_read_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_course_complete_read_id_seq OWNER TO postgres;

--
-- Name: gc_course_complete_read_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_course_complete_read_id_seq OWNED BY arena_hub.gc_course_complete_read.id;


--
-- Name: gc_event; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_event (
    id integer NOT NULL,
    video_id integer NOT NULL,
    event_type smallint,
    event_time integer NOT NULL,
    time_limit integer DEFAULT 0 NOT NULL,
    ext json,
    event_title text,
    link_file_id integer,
    link_video_id integer,
    future_pre_event_id integer,
    future_delay_time double precision,
    "order" integer DEFAULT 0 NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    upload_type integer DEFAULT 1,
    upload_user integer,
    answer_message_flag integer
);


ALTER TABLE arena_hub.gc_event OWNER TO postgres;

--
-- Name: TABLE gc_event; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_event IS '视频下的event';


--
-- Name: COLUMN gc_event.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.id IS 'id';


--
-- Name: COLUMN gc_event.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.video_id IS '视频ID';


--
-- Name: COLUMN gc_event.event_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.event_type IS '问题类型';


--
-- Name: COLUMN gc_event.event_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.event_time IS '弹出时间点，单位秒';


--
-- Name: COLUMN gc_event.time_limit; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.time_limit IS '操作时间限制，单位秒';


--
-- Name: COLUMN gc_event.ext; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.ext IS '额外字段';


--
-- Name: COLUMN gc_event.link_file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.link_file_id IS '事件从sys_file获取的链接';


--
-- Name: COLUMN gc_event.link_video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.link_video_id IS '事件链接视频gc_video的id';


--
-- Name: COLUMN gc_event.future_pre_event_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.future_pre_event_id IS '未来事件的前置事件id';


--
-- Name: COLUMN gc_event.future_delay_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.future_delay_time IS '未来事件延迟时间，单位小时';


--
-- Name: COLUMN gc_event."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event."order" IS '排序字段';


--
-- Name: COLUMN gc_event.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.create_time IS '创建时间';


--
-- Name: COLUMN gc_event.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.update_time IS '更新时间';


--
-- Name: COLUMN gc_event.upload_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.upload_type IS '上传类型，1=门户上传，2=老师添加';


--
-- Name: COLUMN gc_event.upload_user; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.upload_user IS '上传人';


--
-- Name: COLUMN gc_event.answer_message_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_event.answer_message_flag IS '回答问题是否弹出消息提示正确或错误';


--
-- Name: gc_event_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_event_id_seq OWNER TO postgres;

--
-- Name: gc_event_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_event_id_seq OWNED BY arena_hub.gc_event.id;


--
-- Name: gc_faq; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_faq (
    id integer NOT NULL,
    faq_section text NOT NULL,
    faq_title text NOT NULL,
    faq_content text NOT NULL,
    del_flag integer DEFAULT 1 NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    faq_id integer,
    faq_order integer
);


ALTER TABLE arena_hub.gc_faq OWNER TO postgres;

--
-- Name: COLUMN gc_faq.faq_section; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.faq_section IS '常见问题类别';


--
-- Name: COLUMN gc_faq.faq_title; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.faq_title IS '常见问题名称';


--
-- Name: COLUMN gc_faq.faq_content; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.faq_content IS '常见问题回答';


--
-- Name: COLUMN gc_faq.del_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.del_flag IS '0为关闭';


--
-- Name: COLUMN gc_faq.faq_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.faq_id IS '父级常见问题id';


--
-- Name: COLUMN gc_faq.faq_order; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_faq.faq_order IS '排序字段';


--
-- Name: gc_faq_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_faq_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_faq_id_seq OWNER TO postgres;

--
-- Name: gc_faq_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_faq_id_seq OWNED BY arena_hub.gc_faq.id;


--
-- Name: gc_feed_back; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_feed_back (
    id integer NOT NULL,
    user_id integer NOT NULL,
    type integer NOT NULL,
    context text,
    file_json json,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_feed_back OWNER TO postgres;

--
-- Name: COLUMN gc_feed_back.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.id IS '主键id';


--
-- Name: COLUMN gc_feed_back.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.user_id IS '用户id';


--
-- Name: COLUMN gc_feed_back.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.type IS '反馈类型type 0为反馈 1为联系';


--
-- Name: COLUMN gc_feed_back.context; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.context IS '反馈内容';


--
-- Name: COLUMN gc_feed_back.file_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.file_json IS '文件id json格式';


--
-- Name: COLUMN gc_feed_back.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.update_time IS '修改时间';


--
-- Name: COLUMN gc_feed_back.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_feed_back.create_time IS '创建时间';


--
-- Name: gc_feed_back_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_feed_back_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_feed_back_id_seq OWNER TO postgres;

--
-- Name: gc_feed_back_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_feed_back_id_seq OWNED BY arena_hub.gc_feed_back.id;


--
-- Name: gc_group; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_group (
    id integer NOT NULL,
    name text DEFAULT ''::text NOT NULL,
    user_access_id integer NOT NULL,
    master_id integer NOT NULL,
    sub_ids json NOT NULL,
    code text,
    schedule_date json NOT NULL,
    group_access_ids json NOT NULL,
    access_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_group OWNER TO postgres;

--
-- Name: TABLE gc_group; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_group IS '教师编辑的组权限';


--
-- Name: COLUMN gc_group.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.id IS 'id';


--
-- Name: COLUMN gc_group.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.name IS '组名';


--
-- Name: COLUMN gc_group.user_access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.user_access_id IS '创建教师的身份';


--
-- Name: COLUMN gc_group.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.master_id IS '创建组的所在空间';


--
-- Name: COLUMN gc_group.sub_ids; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.sub_ids IS '组的课程权限';


--
-- Name: COLUMN gc_group.code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.code IS 'code';


--
-- Name: COLUMN gc_group.schedule_date; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.schedule_date IS '日程安排分组';


--
-- Name: COLUMN gc_group.group_access_ids; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.group_access_ids IS '组的学生列表';


--
-- Name: COLUMN gc_group.access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.access_id IS '初始化绑定code的gc_access的id';


--
-- Name: COLUMN gc_group.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.update_time IS '更新时间';


--
-- Name: COLUMN gc_group.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_group.create_time IS '创建时间';


--
-- Name: gc_group_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_group_id_seq OWNER TO postgres;

--
-- Name: gc_group_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_group_id_seq OWNED BY arena_hub.gc_group.id;


--
-- Name: gc_group_mentor; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_group_mentor (
    id integer NOT NULL,
    group_id integer,
    user_access_id integer,
    off_flag integer
);


ALTER TABLE arena_hub.gc_group_mentor OWNER TO postgres;

--
-- Name: gc_group_mentor_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_group_mentor_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_group_mentor_id_seq OWNER TO postgres;

--
-- Name: gc_group_mentor_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_group_mentor_id_seq OWNED BY arena_hub.gc_group_mentor.id;


--
-- Name: gc_manager; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_manager (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    last_name text DEFAULT ''::text NOT NULL,
    first_name text DEFAULT ''::text NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    salt text NOT NULL,
    state integer NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    level integer DEFAULT 0,
    master_id integer,
    super_admin_flag integer
);


ALTER TABLE arena_hub.gc_manager OWNER TO postgres;

--
-- Name: TABLE gc_manager; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_manager IS '商户管理员';


--
-- Name: COLUMN gc_manager.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.id IS '主键';


--
-- Name: COLUMN gc_manager.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.sys_id IS '实例id';


--
-- Name: COLUMN gc_manager.last_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.last_name IS '名字';


--
-- Name: COLUMN gc_manager.first_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.first_name IS '名字';


--
-- Name: COLUMN gc_manager.username; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.username IS '用户名';


--
-- Name: COLUMN gc_manager.password; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.password IS '密码';


--
-- Name: COLUMN gc_manager.salt; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.salt IS '密码盐';


--
-- Name: COLUMN gc_manager.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.state IS '状态';


--
-- Name: COLUMN gc_manager.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.update_time IS '更新时间';


--
-- Name: COLUMN gc_manager.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.create_time IS '创建时间';


--
-- Name: COLUMN gc_manager.level; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.level IS '0=门户管理员，1=门户用户';


--
-- Name: COLUMN gc_manager.super_admin_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager.super_admin_flag IS '0或null为普通门户管理员，1为超级管理员';


--
-- Name: gc_manager_collection; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_manager_collection (
    id integer NOT NULL,
    last_name text,
    first_name text,
    email text,
    phone text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_manager_collection OWNER TO postgres;

--
-- Name: TABLE gc_manager_collection; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_manager_collection IS '预注册客户收集';


--
-- Name: COLUMN gc_manager_collection.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager_collection.update_time IS '更新时间';


--
-- Name: COLUMN gc_manager_collection.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_manager_collection.create_time IS '创建时间';


--
-- Name: gc_manager_collection_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_manager_collection_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_manager_collection_id_seq OWNER TO postgres;

--
-- Name: gc_manager_collection_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_manager_collection_id_seq OWNED BY arena_hub.gc_manager_collection.id;


--
-- Name: gc_manager_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_manager_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_manager_id_seq OWNER TO postgres;

--
-- Name: gc_manager_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_manager_id_seq OWNED BY arena_hub.gc_manager.id;


--
-- Name: gc_master; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master (
    id integer NOT NULL,
    manager_id integer,
    state smallint NOT NULL,
    org_name text DEFAULT ''::text NOT NULL,
    portal_name text DEFAULT ''::text NOT NULL,
    context text,
    logo_id integer DEFAULT 0 NOT NULL,
    logo_url text DEFAULT ''::text NOT NULL,
    template_id integer DEFAULT 1 NOT NULL,
    intro_video_id integer,
    source_type smallint,
    source_url text,
    ext_var json,
    intro_done_step json,
    calendar_link text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    color text,
    profile_photo_id integer,
    brand_description text,
    brand_tagline text,
    answer_show_flag integer,
    connected_account_id text,
    ability_split_funds_flag integer,
    email_cc json,
    admin_logo_file_id integer,
    board_id bigint,
    favicon_logo_file_id integer,
    sso_url text,
    sso_client_id text,
    sso_secret text,
    channel_ids json,
    redirect_url text,
    gpt_state smallint
);


ALTER TABLE arena_hub.gc_master OWNER TO postgres;

--
-- Name: TABLE gc_master; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_master IS '主站点实例';


--
-- Name: COLUMN gc_master.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.id IS 'id';


--
-- Name: COLUMN gc_master.manager_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.manager_id IS '管理员Id';


--
-- Name: COLUMN gc_master.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.state IS '状态0，1。空间是否关闭';


--
-- Name: COLUMN gc_master.org_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.org_name IS '机构名称';


--
-- Name: COLUMN gc_master.portal_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.portal_name IS '站点标题';


--
-- Name: COLUMN gc_master.context; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.context IS '站点context';


--
-- Name: COLUMN gc_master.logo_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.logo_id IS '文件Id';


--
-- Name: COLUMN gc_master.logo_url; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.logo_url IS 'logo图片url';


--
-- Name: COLUMN gc_master.template_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.template_id IS '首页样式id';


--
-- Name: COLUMN gc_master.intro_video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.intro_video_id IS '门户介绍视频id';


--
-- Name: COLUMN gc_master.source_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.source_type IS 'null或1:本地intro_video_id不为空，2:youku，3:screenRock，2和3时intro_video_id为空，source_url不为空';


--
-- Name: COLUMN gc_master.source_url; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.source_url IS '源地址  当第三方视频引用的时候';


--
-- Name: COLUMN gc_master.ext_var; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.ext_var IS '额外的变量';


--
-- Name: COLUMN gc_master.intro_done_step; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.intro_done_step IS '门户指引步骤完成情况';


--
-- Name: COLUMN gc_master.calendar_link; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.calendar_link IS '外部日历链接';


--
-- Name: COLUMN gc_master.color; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.color IS '主题颜色参数';


--
-- Name: COLUMN gc_master.answer_show_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.answer_show_flag IS '1为展示，0或null为不展示';


--
-- Name: COLUMN gc_master.email_cc; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.email_cc IS '抄送邮件地址';


--
-- Name: COLUMN gc_master.admin_logo_file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.admin_logo_file_id IS 'pt门户端logoid';


--
-- Name: COLUMN gc_master.gpt_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master.gpt_state IS '默认关闭 开=1，关=null、-1、无值';


--
-- Name: gc_master_active; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_active (
    id integer NOT NULL,
    master_id integer NOT NULL,
    type smallint NOT NULL,
    ret_id integer,
    activate_size numeric(10,2) NOT NULL,
    video_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_master_active OWNER TO postgres;

--
-- Name: TABLE gc_master_active; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_master_active IS 'Master里面资源解锁的配置表';


--
-- Name: COLUMN gc_master_active.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_active.master_id IS '主站id';


--
-- Name: COLUMN gc_master_active.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_active.type IS '0:主站全部解锁1:sub,2:video';


--
-- Name: COLUMN gc_master_active.ret_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_active.ret_id IS '根据type,对应的id';


--
-- Name: COLUMN gc_master_active.activate_size; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_active.activate_size IS '解锁要求';


--
-- Name: COLUMN gc_master_active.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_active.video_id IS '视频id';


--
-- Name: gc_master_active_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_active_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_active_id_seq OWNER TO postgres;

--
-- Name: gc_master_active_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_active_id_seq OWNED BY arena_hub.gc_master_active.id;


--
-- Name: gc_master_home_info; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_home_info (
    id integer NOT NULL,
    master_id integer NOT NULL,
    name text DEFAULT ''::text NOT NULL,
    "order" integer,
    type smallint,
    content text,
    file_id integer,
    state smallint,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    channel_ids json
);


ALTER TABLE arena_hub.gc_master_home_info OWNER TO postgres;

--
-- Name: TABLE gc_master_home_info; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_master_home_info IS '门户首页信息';


--
-- Name: COLUMN gc_master_home_info.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.id IS 'id';


--
-- Name: COLUMN gc_master_home_info.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.master_id IS '门户Id';


--
-- Name: COLUMN gc_master_home_info.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.name IS '内容名称，';


--
-- Name: COLUMN gc_master_home_info."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info."order" IS '各组内部排序';


--
-- Name: COLUMN gc_master_home_info.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.type IS '文件类型-枚举';


--
-- Name: COLUMN gc_master_home_info.content; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.content IS '文字内容';


--
-- Name: COLUMN gc_master_home_info.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.file_id IS 'type=2或3时的文件id';


--
-- Name: COLUMN gc_master_home_info.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_home_info.state IS '逻辑删除,-1=关闭';


--
-- Name: gc_master_home_info_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_home_info_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_home_info_id_seq OWNER TO postgres;

--
-- Name: gc_master_home_info_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_home_info_id_seq OWNED BY arena_hub.gc_master_home_info.id;


--
-- Name: gc_master_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_id_seq OWNER TO postgres;

--
-- Name: gc_master_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_id_seq OWNED BY arena_hub.gc_master.id;


--
-- Name: gc_master_log; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_log (
    id integer NOT NULL,
    manager_id integer NOT NULL,
    master_id integer NOT NULL,
    action_type smallint NOT NULL,
    action_message json NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_master_log OWNER TO postgres;

--
-- Name: TABLE gc_master_log; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_master_log IS '管理员操作日志';


--
-- Name: gc_master_log_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_log_id_seq OWNER TO postgres;

--
-- Name: gc_master_log_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_log_id_seq OWNED BY arena_hub.gc_master_log.id;


--
-- Name: gc_master_message; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_message (
    id integer NOT NULL,
    user_id integer NOT NULL,
    master_id integer NOT NULL,
    target_user_id integer,
    event_type smallint,
    res_id integer,
    user_answer_id integer,
    user_schedule_id integer,
    video_comment_id integer,
    user_note_comment_id integer,
    message text DEFAULT ''::text NOT NULL,
    read_state boolean NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_master_message OWNER TO postgres;

--
-- Name: TABLE gc_master_message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_master_message IS '空间中系统通知事件';


--
-- Name: COLUMN gc_master_message.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.user_id IS '影响的主体用户：该用户导致事件发生';


--
-- Name: COLUMN gc_master_message.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.master_id IS '空间id';


--
-- Name: COLUMN gc_master_message.event_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.event_type IS '0文字,1图片,2视频,3声音,4文件,5问题回答,6ScreenRock链接,与gc_user_event_resource的type一致';


--
-- Name: COLUMN gc_master_message.res_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.res_id IS 'gc_user_event_resource的id';


--
-- Name: COLUMN gc_master_message.user_answer_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.user_answer_id IS 'gc_user_answer的id';


--
-- Name: COLUMN gc_master_message.user_schedule_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.user_schedule_id IS 'gc_user_schedule的id';


--
-- Name: COLUMN gc_master_message.video_comment_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.video_comment_id IS 'gc_video_comment的id 视频评论id';


--
-- Name: COLUMN gc_master_message.user_note_comment_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.user_note_comment_id IS 'gc_user_note_comment的id 作业本回答评论id';


--
-- Name: COLUMN gc_master_message.message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.message IS '消息';


--
-- Name: COLUMN gc_master_message.read_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_message.read_state IS '0:未读1:已读';


--
-- Name: gc_master_message_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_message_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_message_id_seq OWNER TO postgres;

--
-- Name: gc_master_message_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_message_id_seq OWNED BY arena_hub.gc_master_message.id;


--
-- Name: gc_master_pay_records; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_pay_records (
    id integer NOT NULL,
    price numeric(11,2),
    master_id integer,
    subject_id integer,
    user_id integer,
    order_id text,
    state integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.gc_master_pay_records OWNER TO postgres;

--
-- Name: COLUMN gc_master_pay_records.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_pay_records.id IS '主键';


--
-- Name: COLUMN gc_master_pay_records.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_pay_records.subject_id IS '课程id';


--
-- Name: COLUMN gc_master_pay_records.order_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_pay_records.order_id IS '订单号';


--
-- Name: COLUMN gc_master_pay_records.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_pay_records.state IS '购买状态 0 购买失败 1购买成功';


--
-- Name: gc_master_pay_records_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_pay_records_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_pay_records_id_seq OWNER TO postgres;

--
-- Name: gc_master_pay_records_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_pay_records_id_seq OWNED BY arena_hub.gc_master_pay_records.id;


--
-- Name: gc_master_sales_page; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_sales_page (
    id integer NOT NULL,
    section_type_id integer,
    subject_id integer,
    "order" integer,
    context_json text,
    state integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.gc_master_sales_page OWNER TO postgres;

--
-- Name: COLUMN gc_master_sales_page.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page.id IS '主键';


--
-- Name: COLUMN gc_master_sales_page.section_type_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page.section_type_id IS '关联section类型表ID';


--
-- Name: COLUMN gc_master_sales_page.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page.subject_id IS '课程id';


--
-- Name: COLUMN gc_master_sales_page."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page."order" IS '总排序';


--
-- Name: COLUMN gc_master_sales_page.context_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page.context_json IS '模板内容';


--
-- Name: COLUMN gc_master_sales_page.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_sales_page.state IS '状态：0关闭  1启用';


--
-- Name: gc_master_sales_page_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_sales_page_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_sales_page_id_seq OWNER TO postgres;

--
-- Name: gc_master_sales_page_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_sales_page_id_seq OWNED BY arena_hub.gc_master_sales_page.id;


--
-- Name: gc_master_section_type; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_master_section_type (
    id integer NOT NULL,
    section_number integer,
    section_name text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    state integer
);


ALTER TABLE arena_hub.gc_master_section_type OWNER TO postgres;

--
-- Name: COLUMN gc_master_section_type.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_section_type.id IS '主键';


--
-- Name: COLUMN gc_master_section_type.section_number; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_section_type.section_number IS 'section编号';


--
-- Name: COLUMN gc_master_section_type.section_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_section_type.section_name IS 'section名字';


--
-- Name: COLUMN gc_master_section_type.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_master_section_type.state IS '状态：0关闭  1启用';


--
-- Name: gc_master_section_type_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_master_section_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_master_section_type_id_seq OWNER TO postgres;

--
-- Name: gc_master_section_type_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_master_section_type_id_seq OWNED BY arena_hub.gc_master_section_type.id;


--
-- Name: gc_paypal_info; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_paypal_info (
    id integer NOT NULL,
    master_id integer NOT NULL,
    email text,
    username text,
    password text,
    signature text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_paypal_info OWNER TO postgres;

--
-- Name: COLUMN gc_paypal_info.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_paypal_info.master_id IS '门户id';


--
-- Name: gc_paypal_info_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_paypal_info_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_paypal_info_id_seq OWNER TO postgres;

--
-- Name: gc_paypal_info_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_paypal_info_id_seq OWNED BY arena_hub.gc_paypal_info.id;


--
-- Name: gc_problem; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_problem (
    id integer NOT NULL,
    title text NOT NULL,
    context text NOT NULL,
    status integer DEFAULT 1 NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_problem OWNER TO postgres;

--
-- Name: COLUMN gc_problem.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.id IS '主键';


--
-- Name: COLUMN gc_problem.title; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.title IS '常见问题标题';


--
-- Name: COLUMN gc_problem.context; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.context IS '常见问题内容';


--
-- Name: COLUMN gc_problem.status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.status IS '0为关闭';


--
-- Name: COLUMN gc_problem.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.create_time IS '创建时间';


--
-- Name: COLUMN gc_problem.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_problem.update_time IS '修改时间';


--
-- Name: gc_problem_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_problem_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_problem_id_seq OWNER TO postgres;

--
-- Name: gc_problem_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_problem_id_seq OWNED BY arena_hub.gc_problem.id;


--
-- Name: gc_resource; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_resource (
    id integer NOT NULL,
    video_id integer NOT NULL,
    resource_name text,
    file_id integer DEFAULT 0 NOT NULL,
    resource_type integer NOT NULL,
    res_url text DEFAULT ''::text NOT NULL,
    if_end_popup smallint,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_resource OWNER TO postgres;

--
-- Name: COLUMN gc_resource.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.id IS '主键';


--
-- Name: COLUMN gc_resource.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.video_id IS '视频id';


--
-- Name: COLUMN gc_resource.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.file_id IS '文件id';


--
-- Name: COLUMN gc_resource.resource_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.resource_type IS '资源类型 1:文件，2:引用';


--
-- Name: COLUMN gc_resource.res_url; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.res_url IS 'url链接';


--
-- Name: COLUMN gc_resource.if_end_popup; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_resource.if_end_popup IS '是否在视频结束后弹出，1=是';


--
-- Name: gc_resource_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_resource_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_resource_id_seq OWNER TO postgres;

--
-- Name: gc_resource_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_resource_id_seq OWNED BY arena_hub.gc_resource.id;


--
-- Name: gc_social_media; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_social_media (
    id integer NOT NULL,
    master_id integer NOT NULL,
    file_id integer,
    name text NOT NULL,
    link text NOT NULL,
    type integer,
    on_off integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_social_media OWNER TO postgres;

--
-- Name: COLUMN gc_social_media.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.master_id IS '门户id';


--
-- Name: COLUMN gc_social_media.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.file_id IS '文件id';


--
-- Name: COLUMN gc_social_media.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.name IS '社交媒体名称';


--
-- Name: COLUMN gc_social_media.link; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.link IS '链接';


--
-- Name: COLUMN gc_social_media.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.type IS '1=链接，file_id可空，2=图片，link可空';


--
-- Name: COLUMN gc_social_media.on_off; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_social_media.on_off IS '-1=关闭';


--
-- Name: gc_social_media_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_social_media_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_social_media_id_seq OWNER TO postgres;

--
-- Name: gc_social_media_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_social_media_id_seq OWNED BY arena_hub.gc_social_media.id;


--
-- Name: gc_subject; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject (
    id integer NOT NULL,
    master_id integer NOT NULL,
    name text,
    name_index text,
    description text,
    type integer DEFAULT 0 NOT NULL,
    sub_id integer,
    level integer DEFAULT 0 NOT NULL,
    fid integer,
    alias_sub_id integer,
    "order" integer DEFAULT 0 NOT NULL,
    sub_img_id integer,
    state smallint DEFAULT '1'::smallint NOT NULL,
    token text,
    is_public smallint,
    master_type_id json,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    intro_on_off integer,
    template_id integer DEFAULT 1 NOT NULL,
    course_tagline text,
    language text,
    cate_id integer,
    course_tags json,
    certificates_flag integer,
    color text,
    cpd_hours numeric(10,2),
    cpd_flag integer,
    badge_id integer,
    subdetail_img_id json,
    badge_content text,
    create_user integer,
    available_type integer,
    published_user_id integer,
    published_time timestamp with time zone,
    answer_show_flag integer
);


ALTER TABLE arena_hub.gc_subject OWNER TO postgres;

--
-- Name: COLUMN gc_subject.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.id IS '主键';


--
-- Name: COLUMN gc_subject.name_index; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.name_index IS '课程index，单个门户内唯一';


--
-- Name: COLUMN gc_subject.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.type IS '主题类型0为科目1为主题';


--
-- Name: COLUMN gc_subject.level; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.level IS '层级0为顶层';


--
-- Name: COLUMN gc_subject.alias_sub_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.alias_sub_id IS '重命名课程id';


--
-- Name: COLUMN gc_subject."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject."order" IS '排序字段';


--
-- Name: COLUMN gc_subject.sub_img_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.sub_img_id IS 'Subject科目图片';


--
-- Name: COLUMN gc_subject.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.state IS '状态: 0=隐藏，1或空=开';


--
-- Name: COLUMN gc_subject.token; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.token IS '其他课程导入该课程的代码token';


--
-- Name: COLUMN gc_subject.is_public; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.is_public IS '该课程是否是公共课程，1=是，如是其他课程导入该课程不需要token';


--
-- Name: COLUMN gc_subject.master_type_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.master_type_id IS '课程类型id';


--
-- Name: COLUMN gc_subject.intro_on_off; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.intro_on_off IS '是否需要课程介绍页面，1=需要，null=不需要';


--
-- Name: COLUMN gc_subject.template_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.template_id IS '首页样式id';


--
-- Name: COLUMN gc_subject.course_tagline; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.course_tagline IS '课程标语';


--
-- Name: COLUMN gc_subject.color; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.color IS '课程字体颜色';


--
-- Name: COLUMN gc_subject.cpd_hours; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.cpd_hours IS '课程时间';


--
-- Name: COLUMN gc_subject.available_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.available_type IS '发布类型';


--
-- Name: COLUMN gc_subject.published_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.published_user_id IS '发布者id';


--
-- Name: COLUMN gc_subject.published_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.published_time IS '发布时间';


--
-- Name: COLUMN gc_subject.answer_show_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject.answer_show_flag IS '课程回答开关1开,0关,-1跟随门户';


--
-- Name: gc_subject_association; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject_association (
    id integer NOT NULL,
    master_id integer NOT NULL,
    "order" integer DEFAULT 0 NOT NULL,
    subject_id integer,
    state smallint DEFAULT '1'::smallint NOT NULL,
    relation_type smallint,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_subject_association OWNER TO postgres;

--
-- Name: TABLE gc_subject_association; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_subject_association IS '课程门户关联表';


--
-- Name: COLUMN gc_subject_association.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_association.id IS '主键';


--
-- Name: COLUMN gc_subject_association."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_association."order" IS '排序字段';


--
-- Name: COLUMN gc_subject_association.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_association.subject_id IS '关联课程的gc_subject的id';


--
-- Name: COLUMN gc_subject_association.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_association.state IS '状态: 0=隐藏，1或空=开';


--
-- Name: COLUMN gc_subject_association.relation_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_association.relation_type IS '关联课程的关联关系，1=导入import，2=别名alias';


--
-- Name: gc_subject_association_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_association_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_association_id_seq OWNER TO postgres;

--
-- Name: gc_subject_association_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_association_id_seq OWNED BY arena_hub.gc_subject_association.id;


--
-- Name: gc_subject_complete; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject_complete (
    id integer NOT NULL,
    subject_id integer,
    subject_state integer,
    master_id integer,
    user_id integer,
    create_time timestamp with time zone,
    update_time timestamp with time zone,
    in_progress numeric(4,2)
);


ALTER TABLE arena_hub.gc_subject_complete OWNER TO postgres;

--
-- Name: COLUMN gc_subject_complete.subject_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_complete.subject_state IS '完成状态 1完成 0未完成 2已开始';


--
-- Name: COLUMN gc_subject_complete.in_progress; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_complete.in_progress IS '进度';


--
-- Name: gc_subject_complete_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_complete_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_complete_id_seq OWNER TO postgres;

--
-- Name: gc_subject_complete_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_complete_id_seq OWNED BY arena_hub.gc_subject_complete.id;


--
-- Name: gc_subject_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_id_seq OWNER TO postgres;

--
-- Name: gc_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_id_seq OWNED BY arena_hub.gc_subject.id;


--
-- Name: gc_subject_intro_info; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject_intro_info (
    id integer NOT NULL,
    subject_id integer,
    name text,
    content text,
    type smallint,
    file_id integer,
    state smallint,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_subject_intro_info OWNER TO postgres;

--
-- Name: TABLE gc_subject_intro_info; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_subject_intro_info IS '课程介绍实例';


--
-- Name: COLUMN gc_subject_intro_info.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.id IS 'id';


--
-- Name: COLUMN gc_subject_intro_info.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.subject_id IS '课程id';


--
-- Name: COLUMN gc_subject_intro_info.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.name IS '课程介绍页内容名称-枚举';


--
-- Name: COLUMN gc_subject_intro_info.content; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.content IS '课程介绍页文件内容';


--
-- Name: COLUMN gc_subject_intro_info.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.type IS '统一事件类型-枚举';


--
-- Name: COLUMN gc_subject_intro_info.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.file_id IS 'sys_file的id';


--
-- Name: COLUMN gc_subject_intro_info.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_intro_info.state IS '逻辑删除,0=关闭';


--
-- Name: gc_subject_intro_info_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_intro_info_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_intro_info_id_seq OWNER TO postgres;

--
-- Name: gc_subject_intro_info_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_intro_info_id_seq OWNED BY arena_hub.gc_subject_intro_info.id;


--
-- Name: gc_subject_tag_association; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject_tag_association (
    id integer NOT NULL,
    tag_id integer,
    "order" integer,
    master_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    status integer,
    package_status integer,
    close_subject json,
    subject_order json
);


ALTER TABLE arena_hub.gc_subject_tag_association OWNER TO postgres;

--
-- Name: COLUMN gc_subject_tag_association.tag_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.tag_id IS '对应tag表id';


--
-- Name: COLUMN gc_subject_tag_association."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association."order" IS '排序字段';


--
-- Name: COLUMN gc_subject_tag_association.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.master_id IS '门户id';


--
-- Name: COLUMN gc_subject_tag_association.status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.status IS '0启用 1关闭';


--
-- Name: COLUMN gc_subject_tag_association.package_status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.package_status IS '是否仅用于支付包判断 0是 null否';


--
-- Name: COLUMN gc_subject_tag_association.close_subject; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.close_subject IS '关闭展示课程id';


--
-- Name: COLUMN gc_subject_tag_association.subject_order; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tag_association.subject_order IS '导入课程下tag课程排序';


--
-- Name: gc_subject_tag_association_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_tag_association_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_tag_association_id_seq OWNER TO postgres;

--
-- Name: gc_subject_tag_association_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_tag_association_id_seq OWNED BY arena_hub.gc_subject_tag_association.id;


--
-- Name: gc_subject_tags; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_subject_tags (
    id integer NOT NULL,
    master_id integer NOT NULL,
    subject_id integer NOT NULL,
    tag_text text NOT NULL,
    status integer NOT NULL,
    type integer NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "order" integer,
    close_subject json,
    subject_order json
);


ALTER TABLE arena_hub.gc_subject_tags OWNER TO postgres;

--
-- Name: COLUMN gc_subject_tags.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.master_id IS '门户id';


--
-- Name: COLUMN gc_subject_tags.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.subject_id IS '课程id';


--
-- Name: COLUMN gc_subject_tags.tag_text; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.tag_text IS '标签';


--
-- Name: COLUMN gc_subject_tags.status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.status IS '0启用 1关闭';


--
-- Name: COLUMN gc_subject_tags.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.type IS '1=主门户，2=导入的门户';


--
-- Name: COLUMN gc_subject_tags.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.update_time IS '更新时间';


--
-- Name: COLUMN gc_subject_tags.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.create_time IS '创建时间';


--
-- Name: COLUMN gc_subject_tags."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags."order" IS '排序';


--
-- Name: COLUMN gc_subject_tags.close_subject; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.close_subject IS '关闭的课程id';


--
-- Name: COLUMN gc_subject_tags.subject_order; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_subject_tags.subject_order IS 'tag下课程排序';


--
-- Name: gc_subject_tags_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_subject_tags_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_subject_tags_id_seq OWNER TO postgres;

--
-- Name: gc_subject_tags_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_subject_tags_id_seq OWNED BY arena_hub.gc_subject_tags.id;


--
-- Name: gc_user; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    username text DEFAULT ''::text NOT NULL,
    password text DEFAULT ''::text NOT NULL,
    salt text NOT NULL,
    state smallint DEFAULT '1'::smallint NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    pid integer,
    info_id integer,
    pt_user integer,
    is_valid smallint,
    email_code text,
    email_codetime timestamp with time zone
);


ALTER TABLE arena_hub.gc_user OWNER TO postgres;

--
-- Name: COLUMN gc_user.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.id IS '主键';


--
-- Name: COLUMN gc_user.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.sys_id IS '系统id';


--
-- Name: COLUMN gc_user.username; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.username IS '用户名';


--
-- Name: COLUMN gc_user.password; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.password IS '密码';


--
-- Name: COLUMN gc_user.salt; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.salt IS '盐';


--
-- Name: COLUMN gc_user.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.state IS '状态';


--
-- Name: COLUMN gc_user.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.update_time IS '更新时间';


--
-- Name: COLUMN gc_user.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.create_time IS '创建时间';


--
-- Name: COLUMN gc_user.pid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.pid IS '父id：老师的id';


--
-- Name: COLUMN gc_user.info_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.info_id IS '用户信息表ID';


--
-- Name: COLUMN gc_user.pt_user; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.pt_user IS '1为pt用户';


--
-- Name: COLUMN gc_user.is_valid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.is_valid IS '1或null为有效，-1为无效';


--
-- Name: COLUMN gc_user.email_code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.email_code IS '验证邮箱code';


--
-- Name: COLUMN gc_user.email_codetime; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user.email_codetime IS '邮箱code失效时间';


--
-- Name: gc_user_access; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access (
    id integer NOT NULL,
    master_id integer NOT NULL,
    user_id integer,
    access_id integer NOT NULL,
    state smallint DEFAULT '1'::smallint NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    manager_id integer,
    role_json json,
    parent_code text
);


ALTER TABLE arena_hub.gc_user_access OWNER TO postgres;

--
-- Name: TABLE gc_user_access; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_access IS 'Mapping between content groups and assigned courses';


--
-- Name: COLUMN gc_user_access.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.id IS '主键';


--
-- Name: COLUMN gc_user_access.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.master_id IS '主站id';


--
-- Name: COLUMN gc_user_access.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.user_id IS '用户id';


--
-- Name: COLUMN gc_user_access.access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.access_id IS '访问权限id';


--
-- Name: COLUMN gc_user_access.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.state IS '1:正常0:停用';


--
-- Name: COLUMN gc_user_access.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_access.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.create_time IS '创建时间';


--
-- Name: COLUMN gc_user_access.role_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.role_json IS '角色列表';


--
-- Name: COLUMN gc_user_access.parent_code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access.parent_code IS '父级code';


--
-- Name: gc_user_access_active; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_active (
    id integer NOT NULL,
    user_access_id integer NOT NULL,
    vid integer NOT NULL,
    active_amount numeric(10,2) NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_access_active OWNER TO postgres;

--
-- Name: TABLE gc_user_access_active; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_access_active IS '用户解锁视频记录';


--
-- Name: COLUMN gc_user_access_active.vid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_active.vid IS '解锁的视频id';


--
-- Name: COLUMN gc_user_access_active.active_amount; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_active.active_amount IS '解锁的金额';


--
-- Name: gc_user_access_active_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_active_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_active_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_active_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_active_id_seq OWNED BY arena_hub.gc_user_access_active.id;


--
-- Name: gc_user_access_ext; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_ext (
    id integer NOT NULL,
    user_access_id integer,
    log_in_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    points numeric(10,2),
    manager_id integer
);


ALTER TABLE arena_hub.gc_user_access_ext OWNER TO postgres;

--
-- Name: COLUMN gc_user_access_ext.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_ext.id IS '主键';


--
-- Name: COLUMN gc_user_access_ext.user_access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_ext.user_access_id IS 'user_access_id';


--
-- Name: COLUMN gc_user_access_ext.log_in_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_ext.log_in_time IS '登录时间';


--
-- Name: COLUMN gc_user_access_ext.points; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_ext.points IS '用户在门户里面的积分';


--
-- Name: gc_user_access_ext_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_ext_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_ext_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_ext_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_ext_id_seq OWNED BY arena_hub.gc_user_access_ext.id;


--
-- Name: gc_user_access_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_id_seq OWNED BY arena_hub.gc_user_access.id;


--
-- Name: gc_user_access_invite; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_invite (
    id integer NOT NULL,
    user_access_id integer NOT NULL,
    target_access_id integer NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_access_invite OWNER TO postgres;

--
-- Name: TABLE gc_user_access_invite; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_access_invite IS '邀请进来的列表';


--
-- Name: COLUMN gc_user_access_invite.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_invite.id IS '主键';


--
-- Name: COLUMN gc_user_access_invite.target_access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_invite.target_access_id IS '邀请进来的目标id';


--
-- Name: gc_user_access_invite_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_invite_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_invite_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_invite_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_invite_id_seq OWNED BY arena_hub.gc_user_access_invite.id;


--
-- Name: gc_user_access_log; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_log (
    id integer NOT NULL,
    user_access_id integer NOT NULL,
    action_type smallint NOT NULL,
    action_message json NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_access_log OWNER TO postgres;

--
-- Name: TABLE gc_user_access_log; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_access_log IS '用户操作日志';


--
-- Name: COLUMN gc_user_access_log.action_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_log.action_type IS '操作类型';


--
-- Name: COLUMN gc_user_access_log.action_message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_log.action_message IS '操作记录';


--
-- Name: gc_user_access_log_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_log_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_log_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_log_id_seq OWNED BY arena_hub.gc_user_access_log.id;


--
-- Name: gc_user_access_permission; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_permission (
    id integer NOT NULL,
    user_access_id integer NOT NULL,
    sub_permission json NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    short_term_permission json,
    channel_permission json,
    subscribe_permission json,
    must_subject_json json,
    may_subject_json json
);


ALTER TABLE arena_hub.gc_user_access_permission OWNER TO postgres;

--
-- Name: COLUMN gc_user_access_permission.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.id IS 'id';


--
-- Name: COLUMN gc_user_access_permission.user_access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.user_access_id IS '用户userAccessId';


--
-- Name: COLUMN gc_user_access_permission.sub_permission; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.sub_permission IS '用户权限表';


--
-- Name: COLUMN gc_user_access_permission.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_access_permission.short_term_permission; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.short_term_permission IS '非一次性的课程';


--
-- Name: COLUMN gc_user_access_permission.must_subject_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.must_subject_json IS '必须学习的课程';


--
-- Name: COLUMN gc_user_access_permission.may_subject_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_permission.may_subject_json IS '有权限的课程';


--
-- Name: gc_user_access_permission_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_permission_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_permission_id_seq OWNED BY arena_hub.gc_user_access_permission.id;


--
-- Name: gc_user_access_share; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_access_share (
    id integer NOT NULL,
    user_access_id integer NOT NULL,
    share_info json NOT NULL,
    respond_size integer DEFAULT 0 NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_access_share OWNER TO postgres;

--
-- Name: TABLE gc_user_access_share; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_access_share IS '分享记录';


--
-- Name: COLUMN gc_user_access_share.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_share.id IS 'id';


--
-- Name: COLUMN gc_user_access_share.user_access_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_share.user_access_id IS '分享';


--
-- Name: COLUMN gc_user_access_share.share_info; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_share.share_info IS '分享的信息';


--
-- Name: COLUMN gc_user_access_share.respond_size; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_access_share.respond_size IS '被响应的次数';


--
-- Name: gc_user_access_share_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_access_share_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_access_share_id_seq OWNER TO postgres;

--
-- Name: gc_user_access_share_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_access_share_id_seq OWNED BY arena_hub.gc_user_access_share.id;


--
-- Name: gc_user_answer; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_answer (
    id integer NOT NULL,
    event_id integer NOT NULL,
    user_id integer NOT NULL,
    answer_json json NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.gc_user_answer OWNER TO postgres;

--
-- Name: TABLE gc_user_answer; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_answer IS '事件的用户问题回答';


--
-- Name: COLUMN gc_user_answer.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_answer.id IS '主键';


--
-- Name: COLUMN gc_user_answer.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_answer.master_id IS '门户id';


--
-- Name: gc_user_answer_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_answer_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_answer_id_seq OWNER TO postgres;

--
-- Name: gc_user_answer_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_answer_id_seq OWNED BY arena_hub.gc_user_answer.id;


--
-- Name: gc_user_event; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_event (
    id integer NOT NULL,
    user_id integer,
    event_id integer,
    state smallint DEFAULT '1'::smallint NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.gc_user_event OWNER TO postgres;

--
-- Name: TABLE gc_user_event; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_event IS '老师给某些学生及自己添加的问题事件';


--
-- Name: COLUMN gc_user_event.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.user_id IS '用户ID';


--
-- Name: COLUMN gc_user_event.event_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.event_id IS '事件ID';


--
-- Name: COLUMN gc_user_event.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.state IS '1:正常0:停用';


--
-- Name: COLUMN gc_user_event.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_event.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.create_time IS '创建时间';


--
-- Name: COLUMN gc_user_event.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event.master_id IS '门户id';


--
-- Name: gc_user_event_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_event_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_event_id_seq OWNER TO postgres;

--
-- Name: gc_user_event_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_event_id_seq OWNED BY arena_hub.gc_user_event.id;


--
-- Name: gc_user_event_resource; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_event_resource (
    id integer NOT NULL,
    event_id integer NOT NULL,
    target_user_id integer,
    user_id integer NOT NULL,
    target_id integer,
    time_node integer DEFAULT '-1'::integer NOT NULL,
    type smallint,
    content text,
    file_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer,
    super_teacher_message integer,
    super_teacher_all_message integer
);


ALTER TABLE arena_hub.gc_user_event_resource OWNER TO postgres;

--
-- Name: TABLE gc_user_event_resource; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_event_resource IS '事件资源文件';


--
-- Name: COLUMN gc_user_event_resource.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.id IS '主键';


--
-- Name: COLUMN gc_user_event_resource.event_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.event_id IS '问题，事件Id';


--
-- Name: COLUMN gc_user_event_resource.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.user_id IS '用户id';


--
-- Name: COLUMN gc_user_event_resource.time_node; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.time_node IS '时间节点，0:没有时间，单位s';


--
-- Name: COLUMN gc_user_event_resource.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.type IS '0文字,1图片,2视频,3声音,4文件,6ScreenRock链接，与gc_master_message的event_type一致5留空';


--
-- Name: COLUMN gc_user_event_resource.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.master_id IS '门户id';


--
-- Name: COLUMN gc_user_event_resource.super_teacher_message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.super_teacher_message IS '超级老师信息0是 1否';


--
-- Name: COLUMN gc_user_event_resource.super_teacher_all_message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_event_resource.super_teacher_all_message IS '超级老师信息发送全体0是 1否';


--
-- Name: gc_user_event_resource_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_event_resource_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_event_resource_id_seq OWNER TO postgres;

--
-- Name: gc_user_event_resource_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_event_resource_id_seq OWNED BY arena_hub.gc_user_event_resource.id;


--
-- Name: gc_user_fabulous; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_fabulous (
    id integer NOT NULL,
    user_id integer NOT NULL,
    event_id integer,
    video_id integer,
    target_user_id integer NOT NULL,
    comment_id integer NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_fabulous OWNER TO postgres;

--
-- Name: COLUMN gc_user_fabulous.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.user_id IS '角色id';


--
-- Name: COLUMN gc_user_fabulous.event_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.event_id IS '事件id';


--
-- Name: COLUMN gc_user_fabulous.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.video_id IS '视频id';


--
-- Name: COLUMN gc_user_fabulous.target_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.target_user_id IS '被点赞人userId';


--
-- Name: COLUMN gc_user_fabulous.comment_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.comment_id IS '评论id';


--
-- Name: COLUMN gc_user_fabulous.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.update_time IS '修改时间';


--
-- Name: COLUMN gc_user_fabulous.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_fabulous.create_time IS '创建时间';


--
-- Name: gc_user_fabulous_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_fabulous_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_fabulous_id_seq OWNER TO postgres;

--
-- Name: gc_user_fabulous_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_fabulous_id_seq OWNED BY arena_hub.gc_user_fabulous.id;


--
-- Name: gc_user_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_id_seq OWNER TO postgres;

--
-- Name: gc_user_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_id_seq OWNED BY arena_hub.gc_user.id;


--
-- Name: gc_user_info; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_info (
    id integer NOT NULL,
    last_name text,
    first_name text,
    avatar_file_id integer,
    gender smallint,
    phone text,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_info OWNER TO postgres;

--
-- Name: COLUMN gc_user_info.gender; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_info.gender IS '性别，1=男，0=女';


--
-- Name: COLUMN gc_user_info.phone; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_info.phone IS '手机号码';


--
-- Name: COLUMN gc_user_info.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_info.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_info.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_info.create_time IS 'create_time';


--
-- Name: gc_user_info_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_info_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_info_id_seq OWNER TO postgres;

--
-- Name: gc_user_info_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_info_id_seq OWNED BY arena_hub.gc_user_info.id;


--
-- Name: gc_user_message; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_message (
    id bigint NOT NULL,
    user_id integer NOT NULL,
    master_id integer NOT NULL,
    read_state boolean NOT NULL,
    target_user_id integer,
    message text,
    file_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_message OWNER TO postgres;

--
-- Name: COLUMN gc_user_message.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.id IS '主键';


--
-- Name: COLUMN gc_user_message.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.user_id IS '消息所有用户id';


--
-- Name: COLUMN gc_user_message.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.master_id IS '主站id';


--
-- Name: COLUMN gc_user_message.read_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.read_state IS '0:未读1:已读';


--
-- Name: COLUMN gc_user_message.target_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.target_user_id IS '目标用户id';


--
-- Name: COLUMN gc_user_message.message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.message IS '消息内容';


--
-- Name: COLUMN gc_user_message.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.file_id IS '文件id';


--
-- Name: COLUMN gc_user_message.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_message.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_message.create_time IS '创建时间';


--
-- Name: gc_user_message_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_message_id_seq OWNER TO postgres;

--
-- Name: gc_user_message_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_message_id_seq OWNED BY arena_hub.gc_user_message.id;


--
-- Name: gc_user_note; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_note (
    id integer NOT NULL,
    video_id integer NOT NULL,
    user_id integer NOT NULL,
    note_content text,
    file_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.gc_user_note OWNER TO postgres;

--
-- Name: COLUMN gc_user_note.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.id IS '主键';


--
-- Name: COLUMN gc_user_note.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.video_id IS '视频id';


--
-- Name: COLUMN gc_user_note.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.user_id IS '用户id';


--
-- Name: COLUMN gc_user_note.note_content; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.note_content IS '笔记内容';


--
-- Name: COLUMN gc_user_note.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.file_id IS '文件id';


--
-- Name: COLUMN gc_user_note.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_note.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.create_time IS '创建时间';


--
-- Name: COLUMN gc_user_note.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note.master_id IS '门户id';


--
-- Name: gc_user_note_comment; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_note_comment (
    id integer NOT NULL,
    user_id integer NOT NULL,
    target_user_id integer NOT NULL,
    event_id integer NOT NULL,
    context text,
    file_id integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.gc_user_note_comment OWNER TO postgres;

--
-- Name: COLUMN gc_user_note_comment.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.id IS '主键id';


--
-- Name: COLUMN gc_user_note_comment.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.user_id IS '用户id';


--
-- Name: COLUMN gc_user_note_comment.target_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.target_user_id IS '被回复人id';


--
-- Name: COLUMN gc_user_note_comment.event_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.event_id IS '事件id';


--
-- Name: COLUMN gc_user_note_comment.context; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.context IS '回复内容';


--
-- Name: COLUMN gc_user_note_comment.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.file_id IS '上传文件id';


--
-- Name: COLUMN gc_user_note_comment.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.create_time IS '创建时间';


--
-- Name: COLUMN gc_user_note_comment.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.update_time IS '修改时间';


--
-- Name: COLUMN gc_user_note_comment.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_note_comment.master_id IS '门户id';


--
-- Name: gc_user_note_comment_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_note_comment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_note_comment_id_seq OWNER TO postgres;

--
-- Name: gc_user_note_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_note_comment_id_seq OWNED BY arena_hub.gc_user_note_comment.id;


--
-- Name: gc_user_note_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_note_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_note_id_seq OWNER TO postgres;

--
-- Name: gc_user_note_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_note_id_seq OWNED BY arena_hub.gc_user_note.id;


--
-- Name: gc_user_save_content; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_save_content (
    id integer NOT NULL,
    folder_id integer NOT NULL,
    user_id integer NOT NULL,
    master_id integer,
    video_id integer,
    sub_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    file_id integer
);


ALTER TABLE arena_hub.gc_user_save_content OWNER TO postgres;

--
-- Name: TABLE gc_user_save_content; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_save_content IS '用户保存的内容';


--
-- Name: COLUMN gc_user_save_content.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.id IS '主键';


--
-- Name: COLUMN gc_user_save_content.folder_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.folder_id IS '保存文件夹Id';


--
-- Name: COLUMN gc_user_save_content.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.user_id IS '用户Id';


--
-- Name: COLUMN gc_user_save_content.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.master_id IS '门户id';


--
-- Name: COLUMN gc_user_save_content.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.video_id IS '视频Id';


--
-- Name: COLUMN gc_user_save_content.sub_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.sub_id IS '课程Id';


--
-- Name: COLUMN gc_user_save_content.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_content.file_id IS '文件id';


--
-- Name: gc_user_save_content_follow; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_save_content_follow (
    id integer NOT NULL,
    user_id integer,
    folder_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.gc_user_save_content_follow OWNER TO postgres;

--
-- Name: gc_user_save_content_follow_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_save_content_follow_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_save_content_follow_id_seq OWNER TO postgres;

--
-- Name: gc_user_save_content_follow_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_save_content_follow_id_seq OWNED BY arena_hub.gc_user_save_content_follow.id;


--
-- Name: gc_user_save_content_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_save_content_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_save_content_id_seq OWNER TO postgres;

--
-- Name: gc_user_save_content_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_save_content_id_seq OWNED BY arena_hub.gc_user_save_content.id;


--
-- Name: gc_user_save_folder; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_save_folder (
    id integer NOT NULL,
    name text,
    user_id integer NOT NULL,
    state smallint,
    file_id integer,
    master_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    if_private integer
);


ALTER TABLE arena_hub.gc_user_save_folder OWNER TO postgres;

--
-- Name: TABLE gc_user_save_folder; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_save_folder IS '用户保存的视频的文件夹';


--
-- Name: COLUMN gc_user_save_folder.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.id IS '主键';


--
-- Name: COLUMN gc_user_save_folder.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.name IS '文件夹名称';


--
-- Name: COLUMN gc_user_save_folder.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.user_id IS '用户Id';


--
-- Name: COLUMN gc_user_save_folder.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.state IS '状态: 0=隐藏，1或空=开';


--
-- Name: COLUMN gc_user_save_folder.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.file_id IS '封面图片文件id';


--
-- Name: COLUMN gc_user_save_folder.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.master_id IS '门户id';


--
-- Name: COLUMN gc_user_save_folder.if_private; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_save_folder.if_private IS '1代表隐藏，0和null代表显示';


--
-- Name: gc_user_save_folder_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_save_folder_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_save_folder_id_seq OWNER TO postgres;

--
-- Name: gc_user_save_folder_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_save_folder_id_seq OWNED BY arena_hub.gc_user_save_folder.id;


--
-- Name: gc_user_schedule; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_schedule (
    id integer NOT NULL,
    name text,
    user_id integer,
    group_id integer,
    sub_id integer,
    video_id integer,
    level smallint,
    master_id integer NOT NULL,
    schedule_json json NOT NULL,
    addby_user_id integer,
    start_date date,
    end_date date,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_schedule OWNER TO postgres;

--
-- Name: COLUMN gc_user_schedule.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.id IS '主键';


--
-- Name: COLUMN gc_user_schedule.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.name IS '日程计划名称';


--
-- Name: COLUMN gc_user_schedule.group_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.group_id IS '班级id';


--
-- Name: COLUMN gc_user_schedule.sub_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.sub_id IS '课程id';


--
-- Name: COLUMN gc_user_schedule.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.video_id IS '视频id';


--
-- Name: COLUMN gc_user_schedule.level; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.level IS '1=1级课程，2=2级话题，11=视频';


--
-- Name: COLUMN gc_user_schedule.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.master_id IS 'masterId';


--
-- Name: COLUMN gc_user_schedule.schedule_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.schedule_json IS '视频列表';


--
-- Name: COLUMN gc_user_schedule.addby_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.addby_user_id IS '添加老师的id，如果空则自己给自己添加';


--
-- Name: COLUMN gc_user_schedule.start_date; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.start_date IS '开始日期';


--
-- Name: COLUMN gc_user_schedule.end_date; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.end_date IS '结束日期';


--
-- Name: COLUMN gc_user_schedule.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_schedule.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_schedule.create_time IS '创建时间';


--
-- Name: gc_user_schedule_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_schedule_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_schedule_id_seq OWNER TO postgres;

--
-- Name: gc_user_schedule_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_schedule_id_seq OWNED BY arena_hub.gc_user_schedule.id;


--
-- Name: gc_user_stripe; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_stripe (
    id integer NOT NULL,
    type integer,
    stripe_customer_id text,
    user_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_stripe OWNER TO postgres;

--
-- Name: COLUMN gc_user_stripe.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_stripe.type IS '0-stripe';


--
-- Name: gc_user_stripe_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_stripe_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_stripe_id_seq OWNER TO postgres;

--
-- Name: gc_user_stripe_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_stripe_id_seq OWNED BY arena_hub.gc_user_stripe.id;


--
-- Name: gc_user_stripe_subscription; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_stripe_subscription (
    id integer NOT NULL,
    user_stripe_id integer,
    auto_charge_flag integer,
    stripe_subscription_json json,
    stripe_subscription_id text,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    master_id integer
);


ALTER TABLE arena_hub.gc_user_stripe_subscription OWNER TO postgres;

--
-- Name: COLUMN gc_user_stripe_subscription.auto_charge_flag; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_stripe_subscription.auto_charge_flag IS '1代表订阅生效中，0代表不生效';


--
-- Name: COLUMN gc_user_stripe_subscription.stripe_subscription_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_stripe_subscription.stripe_subscription_json IS 'stripe订阅对象数据';


--
-- Name: gc_user_stripe_subscription_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_stripe_subscription_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_stripe_subscription_id_seq OWNER TO postgres;

--
-- Name: gc_user_stripe_subscription_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_stripe_subscription_id_seq OWNED BY arena_hub.gc_user_stripe_subscription.id;


--
-- Name: gc_user_video_action; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_video_action (
    id integer NOT NULL,
    video_id integer,
    user_id integer NOT NULL,
    type smallint,
    value text,
    star_value double precision,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    review_title text,
    review_content text,
    sub_id integer,
    file_id integer
);


ALTER TABLE arena_hub.gc_user_video_action OWNER TO postgres;

--
-- Name: TABLE gc_user_video_action; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_video_action IS '用户对视频的操作，点赞 或者 收藏 等等';


--
-- Name: COLUMN gc_user_video_action.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_action.id IS '主键';


--
-- Name: COLUMN gc_user_video_action.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_action.user_id IS '用户Id';


--
-- Name: COLUMN gc_user_video_action.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_action.type IS '1=点赞，2=评价';


--
-- Name: COLUMN gc_user_video_action.value; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_action.value IS '操作值，1=点赞时为空';


--
-- Name: COLUMN gc_user_video_action.star_value; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_action.star_value IS '星级评价值';


--
-- Name: gc_user_video_action_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_video_action_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_video_action_id_seq OWNER TO postgres;

--
-- Name: gc_user_video_action_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_video_action_id_seq OWNED BY arena_hub.gc_user_video_action.id;


--
-- Name: gc_user_video_play; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_video_play (
    id integer NOT NULL,
    video_id integer,
    file_id integer,
    master_id integer NOT NULL,
    user_id integer NOT NULL,
    play_state smallint NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_video_play OWNER TO postgres;

--
-- Name: TABLE gc_user_video_play; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_user_video_play IS '用户对视频的播放记录';


--
-- Name: COLUMN gc_user_video_play.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.id IS '主键';


--
-- Name: COLUMN gc_user_video_play.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.file_id IS '文件视频id';


--
-- Name: COLUMN gc_user_video_play.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.master_id IS '空间id';


--
-- Name: COLUMN gc_user_video_play.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.user_id IS '用户id';


--
-- Name: COLUMN gc_user_video_play.play_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.play_state IS '0:播放中1:看完';


--
-- Name: COLUMN gc_user_video_play.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_video_play.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_play.create_time IS '创建时间';


--
-- Name: gc_user_video_play_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_video_play_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_video_play_id_seq OWNER TO postgres;

--
-- Name: gc_user_video_play_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_video_play_id_seq OWNED BY arena_hub.gc_user_video_play.id;


--
-- Name: gc_user_video_plays_node; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_video_plays_node (
    id integer NOT NULL,
    videoplay_id integer,
    start_time integer,
    end_time integer DEFAULT 0,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_video_plays_node OWNER TO postgres;

--
-- Name: COLUMN gc_user_video_plays_node.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.id IS '主键';


--
-- Name: COLUMN gc_user_video_plays_node.videoplay_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.videoplay_id IS '视频播放记录表ID';


--
-- Name: COLUMN gc_user_video_plays_node.start_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.start_time IS '开始时间';


--
-- Name: COLUMN gc_user_video_plays_node.end_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.end_time IS '结束时间';


--
-- Name: COLUMN gc_user_video_plays_node.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_video_plays_node.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_video_plays_node.create_time IS '创建时间';


--
-- Name: gc_user_video_plays_node_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_video_plays_node_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_video_plays_node_id_seq OWNER TO postgres;

--
-- Name: gc_user_video_plays_node_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_video_plays_node_id_seq OWNED BY arena_hub.gc_user_video_plays_node.id;


--
-- Name: gc_user_weapp; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_user_weapp (
    id integer NOT NULL,
    uid integer NOT NULL,
    openid text NOT NULL,
    session_key text NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.gc_user_weapp OWNER TO postgres;

--
-- Name: COLUMN gc_user_weapp.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.id IS '主键';


--
-- Name: COLUMN gc_user_weapp.uid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.uid IS '用户id';


--
-- Name: COLUMN gc_user_weapp.openid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.openid IS '微信号';


--
-- Name: COLUMN gc_user_weapp.session_key; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.session_key IS '微信id';


--
-- Name: COLUMN gc_user_weapp.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.update_time IS '更新时间';


--
-- Name: COLUMN gc_user_weapp.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_user_weapp.create_time IS '创建时间';


--
-- Name: gc_user_weapp_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_user_weapp_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_user_weapp_id_seq OWNER TO postgres;

--
-- Name: gc_user_weapp_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_user_weapp_id_seq OWNED BY arena_hub.gc_user_weapp.id;


--
-- Name: gc_video; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_video (
    id integer NOT NULL,
    video_name text,
    video_name_index text,
    video_desc text,
    video_time integer DEFAULT 0 NOT NULL,
    file_id integer DEFAULT 0 NOT NULL,
    source_url text DEFAULT ''::text NOT NULL,
    video_source smallint,
    sub_id integer DEFAULT 0 NOT NULL,
    "order" integer DEFAULT 0 NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    transcript_file_id integer,
    transcript_state smallint DEFAULT '0'::smallint,
    questions json,
    summary text,
    chat_id text,
    lesson_plan text,
    caption_status integer,
    gpt_info json,
    gpt_lang text
);


ALTER TABLE arena_hub.gc_video OWNER TO postgres;

--
-- Name: COLUMN gc_video.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.id IS 'id';


--
-- Name: COLUMN gc_video.video_name_index; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.video_name_index IS '视频index，单个1级课程内唯一';


--
-- Name: COLUMN gc_video.video_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.video_time IS '视频总时长';


--
-- Name: COLUMN gc_video.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.file_id IS '视频源';


--
-- Name: COLUMN gc_video.source_url; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.source_url IS '源地址  当第三方视频引用的时候';


--
-- Name: COLUMN gc_video.video_source; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.video_source IS '1:本地 2:腾讯，3:ScreenRock';


--
-- Name: COLUMN gc_video.sub_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.sub_id IS '课程组织下面';


--
-- Name: COLUMN gc_video."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video."order" IS '排序';


--
-- Name: COLUMN gc_video.transcript_file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.transcript_file_id IS '文件信息';


--
-- Name: COLUMN gc_video.transcript_state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.transcript_state IS '默认为0,0为隐藏1为显示,用于决定是否给用户显示聊天UI';


--
-- Name: COLUMN gc_video.questions; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.questions IS '默认问题';


--
-- Name: COLUMN gc_video.summary; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.summary IS '小结';


--
-- Name: COLUMN gc_video.chat_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.chat_id IS 'chatid';


--
-- Name: COLUMN gc_video.lesson_plan; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.lesson_plan IS '课程计划';


--
-- Name: COLUMN gc_video.caption_status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.caption_status IS '字幕状态';


--
-- Name: COLUMN gc_video.gpt_info; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.gpt_info IS '超时时间处理信息';


--
-- Name: COLUMN gc_video.gpt_lang; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video.gpt_lang IS 'gpt原字幕语言';


--
-- Name: gc_video_comment; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.gc_video_comment (
    id integer NOT NULL,
    video_id integer NOT NULL,
    user_id integer NOT NULL,
    comment text,
    file_id integer,
    reply_comment_id integer,
    main_comment_id integer,
    target_user_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    master_id integer
);


ALTER TABLE arena_hub.gc_video_comment OWNER TO postgres;

--
-- Name: TABLE gc_video_comment; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.gc_video_comment IS '视频评论';


--
-- Name: COLUMN gc_video_comment.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.id IS '主键';


--
-- Name: COLUMN gc_video_comment.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.video_id IS '视频id';


--
-- Name: COLUMN gc_video_comment.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.user_id IS '用户id';


--
-- Name: COLUMN gc_video_comment.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.file_id IS '文件id';


--
-- Name: COLUMN gc_video_comment.reply_comment_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.reply_comment_id IS '被回复的信息id';


--
-- Name: COLUMN gc_video_comment.main_comment_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.main_comment_id IS '主评论id';


--
-- Name: COLUMN gc_video_comment.target_user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.target_user_id IS '被回复的人id';


--
-- Name: COLUMN gc_video_comment.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.gc_video_comment.master_id IS '门户id';


--
-- Name: gc_video_comment_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_video_comment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_video_comment_id_seq OWNER TO postgres;

--
-- Name: gc_video_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_video_comment_id_seq OWNED BY arena_hub.gc_video_comment.id;


--
-- Name: gc_video_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.gc_video_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.gc_video_id_seq OWNER TO postgres;

--
-- Name: gc_video_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.gc_video_id_seq OWNED BY arena_hub.gc_video.id;


--
-- Name: help_top; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.help_top (
    id integer NOT NULL
);


ALTER TABLE arena_hub.help_top OWNER TO postgres;

--
-- Name: pt_channel; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_channel (
    id integer NOT NULL,
    channel_name text,
    channel_img_file_id integer,
    "desc" text,
    create_user_id integer,
    visible_flag integer,
    category_id integer,
    level integer,
    fid integer,
    "order" integer,
    master_id integer,
    channel_slug text,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    channel_avatar_file_id integer
);


ALTER TABLE arena_hub.pt_channel OWNER TO postgres;

--
-- Name: pt_channel_content; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_channel_content (
    id integer NOT NULL,
    channel_id integer,
    file_id integer,
    playlist_id integer,
    section_id integer,
    content_order integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.pt_channel_content OWNER TO postgres;

--
-- Name: pt_channel_content_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_channel_content_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_channel_content_id_seq OWNER TO postgres;

--
-- Name: pt_channel_content_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_channel_content_id_seq OWNED BY arena_hub.pt_channel_content.id;


--
-- Name: pt_channel_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_channel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_channel_id_seq OWNER TO postgres;

--
-- Name: pt_channel_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_channel_id_seq OWNED BY arena_hub.pt_channel.id;


--
-- Name: pt_channel_subscribe; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_channel_subscribe (
    id integer NOT NULL,
    user_id integer,
    channel_id integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.pt_channel_subscribe OWNER TO postgres;

--
-- Name: pt_channel_subscribe_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_channel_subscribe_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_channel_subscribe_id_seq OWNER TO postgres;

--
-- Name: pt_channel_subscribe_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_channel_subscribe_id_seq OWNED BY arena_hub.pt_channel_subscribe.id;


--
-- Name: pt_config; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_config (
    id integer NOT NULL,
    master_id integer,
    show_playlists integer,
    show_search_bar integer,
    page_header text,
    page_body text,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.pt_config OWNER TO postgres;

--
-- Name: pt_config_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_config_id_seq OWNER TO postgres;

--
-- Name: pt_config_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_config_id_seq OWNED BY arena_hub.pt_config.id;


--
-- Name: pt_login_config; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_login_config (
    id integer NOT NULL,
    master_id integer,
    pt_root_url text,
    client_id text,
    oauth_token text,
    user_url text,
    log_out text,
    client_secret text,
    log_out_url text,
    create_time timestamp with time zone,
    update_time timestamp with time zone,
    create_by text,
    update_by text,
    groups text
);


ALTER TABLE arena_hub.pt_login_config OWNER TO postgres;

--
-- Name: COLUMN pt_login_config.groups; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_login_config.groups IS '组接口';


--
-- Name: pt_login_config_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_login_config_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_login_config_id_seq OWNER TO postgres;

--
-- Name: pt_login_config_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_login_config_id_seq OWNED BY arena_hub.pt_login_config.id;


--
-- Name: pt_tags; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_tags (
    id integer NOT NULL,
    master_id integer NOT NULL,
    subject_id integer,
    tag_text text NOT NULL,
    type integer NOT NULL,
    "order" integer,
    video_id integer,
    resource_id integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    channel_id integer,
    file_id integer
);


ALTER TABLE arena_hub.pt_tags OWNER TO postgres;

--
-- Name: COLUMN pt_tags.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.master_id IS '门户id';


--
-- Name: COLUMN pt_tags.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.subject_id IS '课程id';


--
-- Name: COLUMN pt_tags.tag_text; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.tag_text IS '标签';


--
-- Name: COLUMN pt_tags.type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.type IS '1=课程tag 2=视频tag 3=资源tag';


--
-- Name: COLUMN pt_tags."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags."order" IS '排序';


--
-- Name: COLUMN pt_tags.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.video_id IS '视频id';


--
-- Name: COLUMN pt_tags.resource_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.resource_id IS '资源id';


--
-- Name: COLUMN pt_tags.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.update_time IS '更新时间';


--
-- Name: COLUMN pt_tags.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.create_time IS '创建时间';


--
-- Name: COLUMN pt_tags.file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_tags.file_id IS '文件id';


--
-- Name: pt_tags_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_tags_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_tags_id_seq OWNER TO postgres;

--
-- Name: pt_tags_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_tags_id_seq OWNED BY arena_hub.pt_tags.id;


--
-- Name: pt_view_subject; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.pt_view_subject (
    id integer NOT NULL,
    subject_id integer,
    user_id integer,
    master_id integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE arena_hub.pt_view_subject OWNER TO postgres;

--
-- Name: COLUMN pt_view_subject.subject_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_view_subject.subject_id IS '课程id';


--
-- Name: COLUMN pt_view_subject.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_view_subject.user_id IS '用户id';


--
-- Name: COLUMN pt_view_subject.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.pt_view_subject.master_id IS '门户id';


--
-- Name: pt_view_subject_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.pt_view_subject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.pt_view_subject_id_seq OWNER TO postgres;

--
-- Name: pt_view_subject_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.pt_view_subject_id_seq OWNED BY arena_hub.pt_view_subject.id;


--
-- Name: schema_version; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.schema_version (
    id integer NOT NULL,
    version integer DEFAULT 0,
    git_hash text,
    description text,
    installed timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.schema_version OWNER TO postgres;

--
-- Name: schema_version_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.schema_version_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.schema_version_id_seq OWNER TO postgres;

--
-- Name: schema_version_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.schema_version_id_seq OWNED BY arena_hub.schema_version.id;


--
-- Name: sys_business; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_business (
    id integer NOT NULL,
    name text NOT NULL,
    key text NOT NULL,
    remark text DEFAULT ''::text NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_business OWNER TO postgres;

--
-- Name: TABLE sys_business; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_business IS '业务表，用来描述业务';


--
-- Name: COLUMN sys_business.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_business.id IS '业务id';


--
-- Name: COLUMN sys_business.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_business.name IS '业务名称';


--
-- Name: COLUMN sys_business.key; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_business.key IS '业务代码';


--
-- Name: COLUMN sys_business.remark; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_business.remark IS '留言';


--
-- Name: sys_business_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_business_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_business_id_seq OWNER TO postgres;

--
-- Name: sys_business_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_business_id_seq OWNED BY arena_hub.sys_business.id;


--
-- Name: sys_file; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_file (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    upload_uid integer,
    user_role integer,
    name text,
    folder text NOT NULL,
    file_url text,
    file_type text DEFAULT ''::text NOT NULL,
    file_type_index integer,
    file_remark json NOT NULL,
    master_id integer,
    save_type smallint NOT NULL,
    md5 text,
    size text,
    video_long integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    if_caption text,
    target_lang_json text,
    thumb_nail_url text,
    thumb_nail_id integer,
    upload_file_type text,
    description text,
    uuid text,
    webm_to_mp4 json
);


ALTER TABLE arena_hub.sys_file OWNER TO postgres;

--
-- Name: COLUMN sys_file.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.id IS '主键';


--
-- Name: COLUMN sys_file.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.sys_id IS '业务实例id';


--
-- Name: COLUMN sys_file.upload_uid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.upload_uid IS '上传者Id';


--
-- Name: COLUMN sys_file.user_role; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.user_role IS 'upload_uid的角色，1=门户，2=用户';


--
-- Name: COLUMN sys_file.folder; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.folder IS '文件夹目录';


--
-- Name: COLUMN sys_file.file_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.file_type IS '文件类型';


--
-- Name: COLUMN sys_file.file_type_index; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.file_type_index IS '文件类型，枚举';


--
-- Name: COLUMN sys_file.file_remark; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.file_remark IS '文件元数据json';


--
-- Name: COLUMN sys_file.master_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.master_id IS 'gc_master的id';


--
-- Name: COLUMN sys_file.save_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.save_type IS '文件储存方式\n1 disk\n2 \n3链接';


--
-- Name: COLUMN sys_file.size; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.size IS '文件大小';


--
-- Name: COLUMN sys_file.video_long; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.video_long IS '视频或音频时长，视频时适用';


--
-- Name: COLUMN sys_file.if_caption; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.if_caption IS '是否开启字幕';


--
-- Name: COLUMN sys_file.target_lang_json; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.target_lang_json IS '翻译后的字幕文件语言';


--
-- Name: COLUMN sys_file.thumb_nail_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.thumb_nail_id IS '缩略图id';


--
-- Name: COLUMN sys_file.upload_file_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.upload_file_type IS '区分阿里oss和aws s3';


--
-- Name: COLUMN sys_file.uuid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.uuid IS '唯一id';


--
-- Name: COLUMN sys_file.webm_to_mp4; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file.webm_to_mp4 IS '转码信息';


--
-- Name: sys_file_caption; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_file_caption (
    id integer NOT NULL,
    ym_task_id text,
    ym_code integer,
    ym_srt_data text,
    ym_message text DEFAULT ''::text,
    lang text,
    video_id integer,
    caption_file_id integer,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    state integer DEFAULT 1
);


ALTER TABLE arena_hub.sys_file_caption OWNER TO postgres;

--
-- Name: COLUMN sys_file_caption.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.id IS '主键';


--
-- Name: COLUMN sys_file_caption.ym_task_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.ym_task_id IS '云猫任务ID';


--
-- Name: COLUMN sys_file_caption.ym_code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.ym_code IS '云猫code';


--
-- Name: COLUMN sys_file_caption.ym_srt_data; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.ym_srt_data IS 'srt数据';


--
-- Name: COLUMN sys_file_caption.ym_message; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.ym_message IS '云猫消息';


--
-- Name: COLUMN sys_file_caption.lang; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.lang IS '原字幕语言';


--
-- Name: COLUMN sys_file_caption.video_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.video_id IS '视频文件ID';


--
-- Name: COLUMN sys_file_caption.caption_file_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.caption_file_id IS '字幕文件ID';


--
-- Name: COLUMN sys_file_caption.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.create_time IS '创建时间';


--
-- Name: COLUMN sys_file_caption.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.update_time IS '更新时间';


--
-- Name: COLUMN sys_file_caption.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_file_caption.state IS '状态0关 1开';


--
-- Name: sys_file_caption_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_file_caption_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_file_caption_id_seq OWNER TO postgres;

--
-- Name: sys_file_caption_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_file_caption_id_seq OWNED BY arena_hub.sys_file_caption.id;


--
-- Name: sys_file_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_file_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_file_id_seq OWNER TO postgres;

--
-- Name: sys_file_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_file_id_seq OWNED BY arena_hub.sys_file.id;


--
-- Name: sys_menu; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_menu (
    id integer NOT NULL,
    name text,
    update_name text,
    "order" integer,
    update_switch integer,
    key text,
    parent_id integer,
    level integer,
    sort integer,
    create_time timestamp with time zone,
    update_time timestamp with time zone,
    remarks text,
    state integer DEFAULT 0,
    master_id integer
);


ALTER TABLE arena_hub.sys_menu OWNER TO postgres;

--
-- Name: COLUMN sys_menu.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_menu.name IS '名称';


--
-- Name: COLUMN sys_menu.update_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_menu.update_name IS '修改后字段';


--
-- Name: COLUMN sys_menu."order"; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_menu."order" IS '排序';


--
-- Name: COLUMN sys_menu.update_switch; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_menu.update_switch IS '修改开关,1是 0否';


--
-- Name: sys_menu_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_menu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_menu_id_seq OWNER TO postgres;

--
-- Name: sys_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_menu_id_seq OWNED BY arena_hub.sys_menu.id;


--
-- Name: sys_module; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_module (
    id integer NOT NULL,
    business_id integer NOT NULL,
    name text NOT NULL,
    module_code text NOT NULL,
    remark text DEFAULT ''::text NOT NULL,
    parent text NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_module OWNER TO postgres;

--
-- Name: TABLE sys_module; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_module IS '系统权限模块表';


--
-- Name: COLUMN sys_module.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.id IS '主键';


--
-- Name: COLUMN sys_module.business_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.business_id IS '业务id';


--
-- Name: COLUMN sys_module.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.name IS '模块名称';


--
-- Name: COLUMN sys_module.module_code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.module_code IS '模块代码';


--
-- Name: COLUMN sys_module.remark; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.remark IS '模块说明';


--
-- Name: COLUMN sys_module.parent; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.parent IS '业务域';


--
-- Name: COLUMN sys_module.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.create_time IS '创建时间';


--
-- Name: COLUMN sys_module.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_module.update_time IS '更新时间';


--
-- Name: sys_module_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_module_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_module_id_seq OWNER TO postgres;

--
-- Name: sys_module_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_module_id_seq OWNED BY arena_hub.sys_module.id;


--
-- Name: sys_permission; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_permission (
    id integer NOT NULL,
    role_id integer NOT NULL,
    module_id integer NOT NULL,
    perm_code text DEFAULT ''::text NOT NULL
);


ALTER TABLE arena_hub.sys_permission OWNER TO postgres;

--
-- Name: TABLE sys_permission; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_permission IS '角色权限表';


--
-- Name: COLUMN sys_permission.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_permission.id IS '主键';


--
-- Name: COLUMN sys_permission.role_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_permission.role_id IS '角色id';


--
-- Name: COLUMN sys_permission.module_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_permission.module_id IS '模块id';


--
-- Name: COLUMN sys_permission.perm_code; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_permission.perm_code IS '权限功能字符';


--
-- Name: sys_permission_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_permission_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_permission_id_seq OWNER TO postgres;

--
-- Name: sys_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_permission_id_seq OWNED BY arena_hub.sys_permission.id;


--
-- Name: sys_role; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_role (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    role_name text DEFAULT ''::text NOT NULL,
    role_key text DEFAULT ''::text NOT NULL,
    super_admin smallint DEFAULT '0'::smallint NOT NULL,
    remark text DEFAULT ''::text NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_role OWNER TO postgres;

--
-- Name: COLUMN sys_role.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.id IS '主键';


--
-- Name: COLUMN sys_role.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.sys_id IS '权限';


--
-- Name: COLUMN sys_role.role_name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.role_name IS '角色名';


--
-- Name: COLUMN sys_role.role_key; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.role_key IS '角色权限字符串';


--
-- Name: COLUMN sys_role.super_admin; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.super_admin IS '1:为超级管理员不进行权限验证';


--
-- Name: COLUMN sys_role.remark; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.remark IS '说明';


--
-- Name: COLUMN sys_role.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.create_time IS '创建时间';


--
-- Name: COLUMN sys_role.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_role.update_time IS '更新时间';


--
-- Name: sys_role_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_role_id_seq OWNER TO postgres;

--
-- Name: sys_role_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_role_id_seq OWNED BY arena_hub.sys_role.id;


--
-- Name: sys_role_menu; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_role_menu (
    id integer NOT NULL,
    role_id integer,
    menu_ids json,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    update_time timestamp with time zone
);


ALTER TABLE arena_hub.sys_role_menu OWNER TO postgres;

--
-- Name: sys_role_menu_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_role_menu_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_role_menu_id_seq OWNER TO postgres;

--
-- Name: sys_role_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_role_menu_id_seq OWNED BY arena_hub.sys_role_menu.id;


--
-- Name: sys_system; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_system (
    id integer NOT NULL,
    business_id integer NOT NULL,
    name text DEFAULT ''::text NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_system OWNER TO postgres;

--
-- Name: TABLE sys_system; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_system IS '系统实例';


--
-- Name: COLUMN sys_system.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_system.id IS '主键';


--
-- Name: COLUMN sys_system.business_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_system.business_id IS '业务ID';


--
-- Name: COLUMN sys_system.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_system.name IS '实例名称';


--
-- Name: COLUMN sys_system.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_system.create_time IS '创建时间';


--
-- Name: COLUMN sys_system.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_system.update_time IS '更新时间';


--
-- Name: sys_system_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_system_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_system_id_seq OWNER TO postgres;

--
-- Name: sys_system_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_system_id_seq OWNED BY arena_hub.sys_system.id;


--
-- Name: sys_user; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_user (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    salt text DEFAULT ''::text NOT NULL,
    name text NOT NULL,
    phone text NOT NULL,
    state smallint DEFAULT '1'::smallint NOT NULL,
    authentication smallint DEFAULT '0'::smallint NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_user OWNER TO postgres;

--
-- Name: TABLE sys_user; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_user IS '系统用户表，统一用户管理
每个用户在学校中的角色会不一样';


--
-- Name: COLUMN sys_user.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.id IS '主键';


--
-- Name: COLUMN sys_user.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.sys_id IS '系统实例Id';


--
-- Name: COLUMN sys_user.username; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.username IS '用户名';


--
-- Name: COLUMN sys_user.password; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.password IS '密码字段';


--
-- Name: COLUMN sys_user.salt; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.salt IS '盐';


--
-- Name: COLUMN sys_user.name; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.name IS '姓名';


--
-- Name: COLUMN sys_user.phone; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.phone IS '手机号码';


--
-- Name: COLUMN sys_user.state; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.state IS '账号的状态';


--
-- Name: COLUMN sys_user.authentication; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.authentication IS '认证状态';


--
-- Name: COLUMN sys_user.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.create_time IS '创建时间';


--
-- Name: COLUMN sys_user.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user.update_time IS '更新时间';


--
-- Name: sys_user_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_user_id_seq OWNER TO postgres;

--
-- Name: sys_user_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_user_id_seq OWNED BY arena_hub.sys_user.id;


--
-- Name: sys_user_role; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_user_role (
    id integer NOT NULL,
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE arena_hub.sys_user_role OWNER TO postgres;

--
-- Name: COLUMN sys_user_role.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_role.id IS '主键';


--
-- Name: COLUMN sys_user_role.user_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_role.user_id IS '用户id';


--
-- Name: COLUMN sys_user_role.role_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_role.role_id IS '角色id';


--
-- Name: sys_user_role_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_user_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_user_role_id_seq OWNER TO postgres;

--
-- Name: sys_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_user_role_id_seq OWNED BY arena_hub.sys_user_role.id;


--
-- Name: sys_user_weapp; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_user_weapp (
    id integer NOT NULL,
    uid integer NOT NULL,
    openid text DEFAULT ''::text NOT NULL,
    session_key text DEFAULT ''::text NOT NULL
);


ALTER TABLE arena_hub.sys_user_weapp OWNER TO postgres;

--
-- Name: TABLE sys_user_weapp; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_user_weapp IS '微信小程序用户';


--
-- Name: COLUMN sys_user_weapp.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_weapp.id IS '主键';


--
-- Name: COLUMN sys_user_weapp.uid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_weapp.uid IS '用户id';


--
-- Name: COLUMN sys_user_weapp.openid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_weapp.openid IS 'openid';


--
-- Name: COLUMN sys_user_weapp.session_key; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_user_weapp.session_key IS '微信小程序登录态';


--
-- Name: sys_user_weapp_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_user_weapp_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_user_weapp_id_seq OWNER TO postgres;

--
-- Name: sys_user_weapp_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_user_weapp_id_seq OWNED BY arena_hub.sys_user_weapp.id;


--
-- Name: sys_weapp; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_weapp (
    id integer NOT NULL,
    sys_id integer NOT NULL,
    appid text DEFAULT ''::text NOT NULL,
    secret text NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


ALTER TABLE arena_hub.sys_weapp OWNER TO postgres;

--
-- Name: TABLE sys_weapp; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON TABLE arena_hub.sys_weapp IS '系统微信appid secret设置';


--
-- Name: COLUMN sys_weapp.id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.id IS '主键';


--
-- Name: COLUMN sys_weapp.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.sys_id IS '系统实例ID';


--
-- Name: COLUMN sys_weapp.appid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.appid IS 'appid';


--
-- Name: COLUMN sys_weapp.secret; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.secret IS 'secret';


--
-- Name: COLUMN sys_weapp.create_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.create_time IS '创建时间';


--
-- Name: COLUMN sys_weapp.update_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weapp.update_time IS '更新时间';


--
-- Name: sys_weapp_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_weapp_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_weapp_id_seq OWNER TO postgres;

--
-- Name: sys_weapp_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_weapp_id_seq OWNED BY arena_hub.sys_weapp.id;


--
-- Name: sys_weixin_pay; Type: TABLE; Schema: arena_hub; Owner: postgres
--

CREATE TABLE arena_hub.sys_weixin_pay (
    id integer NOT NULL,
    order_id text NOT NULL,
    prepay_id text,
    open_id text NOT NULL,
    uid integer DEFAULT 0 NOT NULL,
    sys_id integer,
    currency text,
    total_fee numeric(10,2) NOT NULL,
    order_status smallint NOT NULL,
    pay_type integer,
    update_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    create_time timestamp with time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    access_id integer,
    master_id integer,
    session_id text,
    session_expire_date integer,
    split_funds_flag integer,
    discount_flag integer,
    total_sub_fee integer,
    access_expire_time timestamp with time zone
);


ALTER TABLE arena_hub.sys_weixin_pay OWNER TO postgres;

--
-- Name: COLUMN sys_weixin_pay.order_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.order_id IS '订单号';


--
-- Name: COLUMN sys_weixin_pay.prepay_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.prepay_id IS '微信订单下单号';


--
-- Name: COLUMN sys_weixin_pay.open_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.open_id IS 'openId';


--
-- Name: COLUMN sys_weixin_pay.uid; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.uid IS '用户id';


--
-- Name: COLUMN sys_weixin_pay.sys_id; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.sys_id IS 'sysId';


--
-- Name: COLUMN sys_weixin_pay.currency; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.currency IS '币种';


--
-- Name: COLUMN sys_weixin_pay.total_fee; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.total_fee IS '付款金额';


--
-- Name: COLUMN sys_weixin_pay.order_status; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.order_status IS '订单状态0:未支付，1:完成2:取消';


--
-- Name: COLUMN sys_weixin_pay.pay_type; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.pay_type IS '支付类型 0微信 1支付宝 2paypal 3stripe';


--
-- Name: COLUMN sys_weixin_pay.access_expire_time; Type: COMMENT; Schema: arena_hub; Owner: postgres
--

COMMENT ON COLUMN arena_hub.sys_weixin_pay.access_expire_time IS '订阅过期时间';


--
-- Name: sys_weixin_pay_id_seq; Type: SEQUENCE; Schema: arena_hub; Owner: postgres
--

CREATE SEQUENCE arena_hub.sys_weixin_pay_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE arena_hub.sys_weixin_pay_id_seq OWNER TO postgres;

--
-- Name: sys_weixin_pay_id_seq; Type: SEQUENCE OWNED BY; Schema: arena_hub; Owner: postgres
--

ALTER SEQUENCE arena_hub.sys_weixin_pay_id_seq OWNED BY arena_hub.sys_weixin_pay.id;


--
-- Name: feature_toggle id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.feature_toggle ALTER COLUMN id SET DEFAULT nextval('arena_hub.feature_toggle_id_seq'::regclass);


--
-- Name: gc_access id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_access_id_seq'::regclass);


--
-- Name: gc_chat_history id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_history ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_chat_history_id_seq'::regclass);


--
-- Name: gc_chat_list id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_list ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_chat_list_id_seq'::regclass);


--
-- Name: gc_content_group_course_assignment id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_content_group_course_assignment ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_content_group_course_assignment_id_seq'::regclass);


--
-- Name: gc_course_cate id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_cate ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_course_cate_id_seq'::regclass);


--
-- Name: gc_course_complete_read id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_complete_read ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_course_complete_read_id_seq'::regclass);


--
-- Name: gc_event id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_event_id_seq'::regclass);


--
-- Name: gc_faq id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_faq ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_faq_id_seq'::regclass);


--
-- Name: gc_feed_back id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_feed_back ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_feed_back_id_seq'::regclass);


--
-- Name: gc_group id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_group_id_seq'::regclass);


--
-- Name: gc_group_mentor id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group_mentor ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_group_mentor_id_seq'::regclass);


--
-- Name: gc_manager id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_manager ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_manager_id_seq'::regclass);


--
-- Name: gc_manager_collection id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_manager_collection ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_manager_collection_id_seq'::regclass);


--
-- Name: gc_master id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_id_seq'::regclass);


--
-- Name: gc_master_active id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_active ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_active_id_seq'::regclass);


--
-- Name: gc_master_home_info id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_home_info ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_home_info_id_seq'::regclass);


--
-- Name: gc_master_log id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_log ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_log_id_seq'::regclass);


--
-- Name: gc_master_message id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_message_id_seq'::regclass);


--
-- Name: gc_master_pay_records id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_pay_records ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_pay_records_id_seq'::regclass);


--
-- Name: gc_master_sales_page id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_sales_page ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_sales_page_id_seq'::regclass);


--
-- Name: gc_master_section_type id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_section_type ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_master_section_type_id_seq'::regclass);


--
-- Name: gc_paypal_info id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_paypal_info ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_paypal_info_id_seq'::regclass);


--
-- Name: gc_problem id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_problem ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_problem_id_seq'::regclass);


--
-- Name: gc_resource id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_resource ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_resource_id_seq'::regclass);


--
-- Name: gc_social_media id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_social_media ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_social_media_id_seq'::regclass);


--
-- Name: gc_subject id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_id_seq'::regclass);


--
-- Name: gc_subject_association id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_association ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_association_id_seq'::regclass);


--
-- Name: gc_subject_complete id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_complete ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_complete_id_seq'::regclass);


--
-- Name: gc_subject_intro_info id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_intro_info ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_intro_info_id_seq'::regclass);


--
-- Name: gc_subject_tag_association id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tag_association ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_tag_association_id_seq'::regclass);


--
-- Name: gc_subject_tags id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tags ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_subject_tags_id_seq'::regclass);


--
-- Name: gc_user id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_id_seq'::regclass);


--
-- Name: gc_user_access id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_id_seq'::regclass);


--
-- Name: gc_user_access_active id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_active ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_active_id_seq'::regclass);


--
-- Name: gc_user_access_ext id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_ext ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_ext_id_seq'::regclass);


--
-- Name: gc_user_access_invite id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_invite ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_invite_id_seq'::regclass);


--
-- Name: gc_user_access_log id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_log ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_log_id_seq'::regclass);


--
-- Name: gc_user_access_permission id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_permission ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_permission_id_seq'::regclass);


--
-- Name: gc_user_access_share id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_share ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_access_share_id_seq'::regclass);


--
-- Name: gc_user_answer id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_answer ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_answer_id_seq'::regclass);


--
-- Name: gc_user_event id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_event_id_seq'::regclass);


--
-- Name: gc_user_event_resource id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_event_resource_id_seq'::regclass);


--
-- Name: gc_user_fabulous id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_fabulous_id_seq'::regclass);


--
-- Name: gc_user_info id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_info ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_info_id_seq'::regclass);


--
-- Name: gc_user_message id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_message_id_seq'::regclass);


--
-- Name: gc_user_note id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_note_id_seq'::regclass);


--
-- Name: gc_user_note_comment id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_note_comment_id_seq'::regclass);


--
-- Name: gc_user_save_content id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_save_content_id_seq'::regclass);


--
-- Name: gc_user_save_content_follow id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content_follow ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_save_content_follow_id_seq'::regclass);


--
-- Name: gc_user_save_folder id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_folder ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_save_folder_id_seq'::regclass);


--
-- Name: gc_user_schedule id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_schedule_id_seq'::regclass);


--
-- Name: gc_user_stripe id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_stripe_id_seq'::regclass);


--
-- Name: gc_user_stripe_subscription id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe_subscription ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_stripe_subscription_id_seq'::regclass);


--
-- Name: gc_user_video_action id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_video_action_id_seq'::regclass);


--
-- Name: gc_user_video_play id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_video_play_id_seq'::regclass);


--
-- Name: gc_user_video_plays_node id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_plays_node ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_video_plays_node_id_seq'::regclass);


--
-- Name: gc_user_weapp id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_weapp ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_user_weapp_id_seq'::regclass);


--
-- Name: gc_video id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_video_id_seq'::regclass);


--
-- Name: gc_video_comment id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment ALTER COLUMN id SET DEFAULT nextval('arena_hub.gc_video_comment_id_seq'::regclass);


--
-- Name: pt_channel id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_channel_id_seq'::regclass);


--
-- Name: pt_channel_content id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_content ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_channel_content_id_seq'::regclass);


--
-- Name: pt_channel_subscribe id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_subscribe ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_channel_subscribe_id_seq'::regclass);


--
-- Name: pt_config id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_config ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_config_id_seq'::regclass);


--
-- Name: pt_login_config id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_login_config ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_login_config_id_seq'::regclass);


--
-- Name: pt_tags id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_tags_id_seq'::regclass);


--
-- Name: pt_view_subject id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_view_subject ALTER COLUMN id SET DEFAULT nextval('arena_hub.pt_view_subject_id_seq'::regclass);


--
-- Name: schema_version id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.schema_version ALTER COLUMN id SET DEFAULT nextval('arena_hub.schema_version_id_seq'::regclass);


--
-- Name: sys_business id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_business ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_business_id_seq'::regclass);


--
-- Name: sys_file id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_file_id_seq'::regclass);


--
-- Name: sys_file_caption id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file_caption ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_file_caption_id_seq'::regclass);


--
-- Name: sys_menu id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_menu ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_menu_id_seq'::regclass);


--
-- Name: sys_module id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_module ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_module_id_seq'::regclass);


--
-- Name: sys_permission id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_permission ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_permission_id_seq'::regclass);


--
-- Name: sys_role id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_role ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_role_id_seq'::regclass);


--
-- Name: sys_role_menu id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_role_menu ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_role_menu_id_seq'::regclass);


--
-- Name: sys_system id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_system ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_system_id_seq'::regclass);


--
-- Name: sys_user id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_user_id_seq'::regclass);


--
-- Name: sys_user_role id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user_role ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_user_role_id_seq'::regclass);


--
-- Name: sys_user_weapp id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user_weapp ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_user_weapp_id_seq'::regclass);


--
-- Name: sys_weapp id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_weapp ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_weapp_id_seq'::regclass);


--
-- Name: sys_weixin_pay id; Type: DEFAULT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_weixin_pay ALTER COLUMN id SET DEFAULT nextval('arena_hub.sys_weixin_pay_id_seq'::regclass);


--
-- Data for Name: feature_toggle; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--

INSERT INTO arena_hub.feature_toggle (id, name, description, value, created_date, modified_date, master_id) VALUES (1, 'coursesEnabled', 'Feature toggle for showing or hiding courses', 'true', '2024-04-10 09:12:58+00', '2024-04-10 09:12:58+00', NULL);
INSERT INTO arena_hub.feature_toggle (id, name, description, value, created_date, modified_date, master_id) VALUES (2, 'analyticsEnabled', 'Feature toggle for showing or hiding analytics', 'true', '2024-04-10 09:12:58+00', '2024-04-10 09:12:58+00', NULL);


--
-- Data for Name: gc_access; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_category; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_chat_history; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_chat_list; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_content_group_course_assignment; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_course_cate; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_course_complete_read; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_event; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_faq; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_feed_back; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_group; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_group_mentor; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_manager; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_manager_collection; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_active; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_home_info; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_log; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_message; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_pay_records; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_sales_page; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_master_section_type; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_paypal_info; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_problem; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_resource; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_social_media; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject_association; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject_complete; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject_intro_info; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject_tag_association; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_subject_tags; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_active; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_ext; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_invite; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_log; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_permission; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_access_share; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_answer; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_event; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_event_resource; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_fabulous; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_info; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_message; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_note; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_note_comment; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_save_content; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_save_content_follow; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_save_folder; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_schedule; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_stripe; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_stripe_subscription; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_video_action; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_video_play; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_video_plays_node; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_user_weapp; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_video; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: gc_video_comment; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: help_top; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_channel; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_channel_content; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_channel_subscribe; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_config; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_login_config; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_tags; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: pt_view_subject; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: schema_version; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_business; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_file; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_file_caption; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_module; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_permission; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_role; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_system; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_user; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_user_weapp; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_weapp; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Data for Name: sys_weixin_pay; Type: TABLE DATA; Schema: arena_hub; Owner: postgres
--



--
-- Name: feature_toggle_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.feature_toggle_id_seq', 2, true);


--
-- Name: gc_access_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_access_id_seq', 1, true);


--
-- Name: gc_chat_history_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_chat_history_id_seq', 1, true);


--
-- Name: gc_chat_list_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_chat_list_id_seq', 1, true);


--
-- Name: gc_content_group_course_assignment_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_content_group_course_assignment_id_seq', 1, true);


--
-- Name: gc_course_cate_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_course_cate_id_seq', 1, true);


--
-- Name: gc_course_complete_read_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_course_complete_read_id_seq', 1, true);


--
-- Name: gc_event_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_event_id_seq', 1, true);


--
-- Name: gc_faq_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_faq_id_seq', 1, true);


--
-- Name: gc_feed_back_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_feed_back_id_seq', 1, true);


--
-- Name: gc_group_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_group_id_seq', 1, true);


--
-- Name: gc_group_mentor_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_group_mentor_id_seq', 1, true);


--
-- Name: gc_manager_collection_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_manager_collection_id_seq', 1, true);


--
-- Name: gc_manager_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_manager_id_seq', 1, true);


--
-- Name: gc_master_active_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_active_id_seq', 1, true);


--
-- Name: gc_master_home_info_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_home_info_id_seq', 1, true);


--
-- Name: gc_master_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_id_seq', 1, true);


--
-- Name: gc_master_log_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_log_id_seq', 1, true);


--
-- Name: gc_master_message_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_message_id_seq', 1, true);


--
-- Name: gc_master_pay_records_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_pay_records_id_seq', 1, true);


--
-- Name: gc_master_sales_page_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_sales_page_id_seq', 1, true);


--
-- Name: gc_master_section_type_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_master_section_type_id_seq', 1, true);


--
-- Name: gc_paypal_info_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_paypal_info_id_seq', 1, true);


--
-- Name: gc_problem_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_problem_id_seq', 1, true);


--
-- Name: gc_resource_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_resource_id_seq', 1, true);


--
-- Name: gc_social_media_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_social_media_id_seq', 1, true);


--
-- Name: gc_subject_association_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_association_id_seq', 1, true);


--
-- Name: gc_subject_complete_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_complete_id_seq', 1, true);


--
-- Name: gc_subject_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_id_seq', 1, true);


--
-- Name: gc_subject_intro_info_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_intro_info_id_seq', 1, true);


--
-- Name: gc_subject_tag_association_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_tag_association_id_seq', 1, true);


--
-- Name: gc_subject_tags_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_subject_tags_id_seq', 1, true);


--
-- Name: gc_user_access_active_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_active_id_seq', 1, true);


--
-- Name: gc_user_access_ext_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_ext_id_seq', 1, true);


--
-- Name: gc_user_access_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_id_seq', 1, true);


--
-- Name: gc_user_access_invite_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_invite_id_seq', 1, true);


--
-- Name: gc_user_access_log_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_log_id_seq', 1, true);


--
-- Name: gc_user_access_permission_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_permission_id_seq', 1, true);


--
-- Name: gc_user_access_share_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_access_share_id_seq', 1, true);


--
-- Name: gc_user_answer_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_answer_id_seq', 1, true);


--
-- Name: gc_user_event_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_event_id_seq', 1, true);


--
-- Name: gc_user_event_resource_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_event_resource_id_seq', 1, true);


--
-- Name: gc_user_fabulous_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_fabulous_id_seq', 1, true);


--
-- Name: gc_user_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_id_seq', 1, true);


--
-- Name: gc_user_info_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_info_id_seq', 1, true);


--
-- Name: gc_user_message_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_message_id_seq', 1, true);


--
-- Name: gc_user_note_comment_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_note_comment_id_seq', 1, true);


--
-- Name: gc_user_note_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_note_id_seq', 1, true);


--
-- Name: gc_user_save_content_follow_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_save_content_follow_id_seq', 1, true);


--
-- Name: gc_user_save_content_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_save_content_id_seq', 1, true);


--
-- Name: gc_user_save_folder_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_save_folder_id_seq', 1, true);


--
-- Name: gc_user_schedule_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_schedule_id_seq', 1, true);


--
-- Name: gc_user_stripe_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_stripe_id_seq', 1, true);


--
-- Name: gc_user_stripe_subscription_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_stripe_subscription_id_seq', 1, true);


--
-- Name: gc_user_video_action_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_video_action_id_seq', 1, true);


--
-- Name: gc_user_video_play_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_video_play_id_seq', 1, true);


--
-- Name: gc_user_video_plays_node_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_video_plays_node_id_seq', 1, true);


--
-- Name: gc_user_weapp_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_user_weapp_id_seq', 1, true);


--
-- Name: gc_video_comment_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_video_comment_id_seq', 1, true);


--
-- Name: gc_video_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.gc_video_id_seq', 1, true);


--
-- Name: pt_channel_content_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_channel_content_id_seq', 1, true);


--
-- Name: pt_channel_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_channel_id_seq', 1, true);


--
-- Name: pt_channel_subscribe_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_channel_subscribe_id_seq', 1, true);


--
-- Name: pt_config_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_config_id_seq', 1, true);


--
-- Name: pt_login_config_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_login_config_id_seq', 1, true);


--
-- Name: pt_tags_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_tags_id_seq', 1, true);


--
-- Name: pt_view_subject_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.pt_view_subject_id_seq', 1, true);


--
-- Name: schema_version_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.schema_version_id_seq', 1, true);


--
-- Name: sys_business_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_business_id_seq', 1, true);


--
-- Name: sys_file_caption_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_file_caption_id_seq', 1, true);


--
-- Name: sys_file_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_file_id_seq', 1, true);


--
-- Name: sys_menu_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_menu_id_seq', 1, true);


--
-- Name: sys_module_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_module_id_seq', 1, true);


--
-- Name: sys_permission_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_permission_id_seq', 1, true);


--
-- Name: sys_role_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_role_id_seq', 1, true);


--
-- Name: sys_role_menu_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_role_menu_id_seq', 1, true);


--
-- Name: sys_system_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_system_id_seq', 1, true);


--
-- Name: sys_user_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_user_id_seq', 1, true);


--
-- Name: sys_user_role_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_user_role_id_seq', 1, true);


--
-- Name: sys_user_weapp_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_user_weapp_id_seq', 1, true);


--
-- Name: sys_weapp_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_weapp_id_seq', 1, true);


--
-- Name: sys_weixin_pay_id_seq; Type: SEQUENCE SET; Schema: arena_hub; Owner: postgres
--

SELECT pg_catalog.setval('arena_hub.sys_weixin_pay_id_seq', 1, true);


--
-- Name: feature_toggle idx_16402_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.feature_toggle
    ADD CONSTRAINT idx_16402_primary PRIMARY KEY (id);


--
-- Name: gc_access idx_16411_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access
    ADD CONSTRAINT idx_16411_primary PRIMARY KEY (id);


--
-- Name: gc_category idx_16420_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_category
    ADD CONSTRAINT idx_16420_primary PRIMARY KEY (id);


--
-- Name: gc_chat_history idx_16426_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_history
    ADD CONSTRAINT idx_16426_primary PRIMARY KEY (id);


--
-- Name: gc_chat_list idx_16435_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_list
    ADD CONSTRAINT idx_16435_primary PRIMARY KEY (id);


--
-- Name: gc_content_group_course_assignment idx_16444_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_content_group_course_assignment
    ADD CONSTRAINT idx_16444_primary PRIMARY KEY (id);


--
-- Name: gc_course_cate idx_16452_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_cate
    ADD CONSTRAINT idx_16452_primary PRIMARY KEY (id);


--
-- Name: gc_course_complete_read idx_16459_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_complete_read
    ADD CONSTRAINT idx_16459_primary PRIMARY KEY (id);


--
-- Name: gc_event idx_16466_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event
    ADD CONSTRAINT idx_16466_primary PRIMARY KEY (id);


--
-- Name: gc_faq idx_16478_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_faq
    ADD CONSTRAINT idx_16478_primary PRIMARY KEY (id);


--
-- Name: gc_feed_back idx_16488_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_feed_back
    ADD CONSTRAINT idx_16488_primary PRIMARY KEY (id);


--
-- Name: gc_group idx_16497_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group
    ADD CONSTRAINT idx_16497_primary PRIMARY KEY (id);


--
-- Name: gc_group_mentor idx_16507_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group_mentor
    ADD CONSTRAINT idx_16507_primary PRIMARY KEY (id);


--
-- Name: gc_manager idx_16512_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_manager
    ADD CONSTRAINT idx_16512_primary PRIMARY KEY (id);


--
-- Name: gc_manager_collection idx_16524_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_manager_collection
    ADD CONSTRAINT idx_16524_primary PRIMARY KEY (id);


--
-- Name: gc_master idx_16533_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master
    ADD CONSTRAINT idx_16533_primary PRIMARY KEY (id);


--
-- Name: gc_master_active idx_16547_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_active
    ADD CONSTRAINT idx_16547_primary PRIMARY KEY (id);


--
-- Name: gc_master_home_info idx_16554_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_home_info
    ADD CONSTRAINT idx_16554_primary PRIMARY KEY (id);


--
-- Name: gc_master_log idx_16564_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_log
    ADD CONSTRAINT idx_16564_primary PRIMARY KEY (id);


--
-- Name: gc_master_message idx_16573_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT idx_16573_primary PRIMARY KEY (id);


--
-- Name: gc_master_pay_records idx_16583_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_pay_records
    ADD CONSTRAINT idx_16583_primary PRIMARY KEY (id);


--
-- Name: gc_master_sales_page idx_16592_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_sales_page
    ADD CONSTRAINT idx_16592_primary PRIMARY KEY (id);


--
-- Name: gc_master_section_type idx_16601_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_section_type
    ADD CONSTRAINT idx_16601_primary PRIMARY KEY (id);


--
-- Name: gc_paypal_info idx_16610_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_paypal_info
    ADD CONSTRAINT idx_16610_primary PRIMARY KEY (id);


--
-- Name: gc_problem idx_16619_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_problem
    ADD CONSTRAINT idx_16619_primary PRIMARY KEY (id);


--
-- Name: gc_resource idx_16629_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_resource
    ADD CONSTRAINT idx_16629_primary PRIMARY KEY (id);


--
-- Name: gc_social_media idx_16640_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_social_media
    ADD CONSTRAINT idx_16640_primary PRIMARY KEY (id);


--
-- Name: gc_subject idx_16649_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT idx_16649_primary PRIMARY KEY (id);


--
-- Name: gc_subject_association idx_16663_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_association
    ADD CONSTRAINT idx_16663_primary PRIMARY KEY (id);


--
-- Name: gc_subject_complete idx_16672_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_complete
    ADD CONSTRAINT idx_16672_primary PRIMARY KEY (id);


--
-- Name: gc_subject_intro_info idx_16677_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_intro_info
    ADD CONSTRAINT idx_16677_primary PRIMARY KEY (id);


--
-- Name: gc_subject_tag_association idx_16686_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tag_association
    ADD CONSTRAINT idx_16686_primary PRIMARY KEY (id);


--
-- Name: gc_subject_tags idx_16695_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tags
    ADD CONSTRAINT idx_16695_primary PRIMARY KEY (id);


--
-- Name: gc_user idx_16704_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user
    ADD CONSTRAINT idx_16704_primary PRIMARY KEY (id);


--
-- Name: gc_user_access idx_16716_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access
    ADD CONSTRAINT idx_16716_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_active idx_16726_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_active
    ADD CONSTRAINT idx_16726_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_ext idx_16733_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_ext
    ADD CONSTRAINT idx_16733_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_invite idx_16739_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_invite
    ADD CONSTRAINT idx_16739_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_log idx_16746_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_log
    ADD CONSTRAINT idx_16746_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_permission idx_16755_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_permission
    ADD CONSTRAINT idx_16755_primary PRIMARY KEY (id);


--
-- Name: gc_user_access_share idx_16764_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_share
    ADD CONSTRAINT idx_16764_primary PRIMARY KEY (id);


--
-- Name: gc_user_answer idx_16774_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_answer
    ADD CONSTRAINT idx_16774_primary PRIMARY KEY (id);


--
-- Name: gc_user_event idx_16783_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event
    ADD CONSTRAINT idx_16783_primary PRIMARY KEY (id);


--
-- Name: gc_user_event_resource idx_16791_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT idx_16791_primary PRIMARY KEY (id);


--
-- Name: gc_user_fabulous idx_16801_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous
    ADD CONSTRAINT idx_16801_primary PRIMARY KEY (id);


--
-- Name: gc_user_info idx_16808_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_info
    ADD CONSTRAINT idx_16808_primary PRIMARY KEY (id);


--
-- Name: gc_user_message idx_16817_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message
    ADD CONSTRAINT idx_16817_primary PRIMARY KEY (id);


--
-- Name: gc_user_note idx_16826_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note
    ADD CONSTRAINT idx_16826_primary PRIMARY KEY (id);


--
-- Name: gc_user_note_comment idx_16835_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT idx_16835_primary PRIMARY KEY (id);


--
-- Name: gc_user_save_content idx_16844_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT idx_16844_primary PRIMARY KEY (id);


--
-- Name: gc_user_save_content_follow idx_16851_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content_follow
    ADD CONSTRAINT idx_16851_primary PRIMARY KEY (id);


--
-- Name: gc_user_save_folder idx_16858_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_folder
    ADD CONSTRAINT idx_16858_primary PRIMARY KEY (id);


--
-- Name: gc_user_schedule idx_16867_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT idx_16867_primary PRIMARY KEY (id);


--
-- Name: gc_user_stripe idx_16876_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe
    ADD CONSTRAINT idx_16876_primary PRIMARY KEY (id);


--
-- Name: gc_user_stripe_subscription idx_16885_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe_subscription
    ADD CONSTRAINT idx_16885_primary PRIMARY KEY (id);


--
-- Name: gc_user_video_action idx_16894_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action
    ADD CONSTRAINT idx_16894_primary PRIMARY KEY (id);


--
-- Name: gc_user_video_play idx_16903_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT idx_16903_primary PRIMARY KEY (id);


--
-- Name: gc_user_video_plays_node idx_16910_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_plays_node
    ADD CONSTRAINT idx_16910_primary PRIMARY KEY (id);


--
-- Name: gc_user_weapp idx_16918_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_weapp
    ADD CONSTRAINT idx_16918_primary PRIMARY KEY (id);


--
-- Name: gc_video idx_16927_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video
    ADD CONSTRAINT idx_16927_primary PRIMARY KEY (id);


--
-- Name: gc_video_comment idx_16942_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT idx_16942_primary PRIMARY KEY (id);


--
-- Name: help_top idx_16950_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.help_top
    ADD CONSTRAINT idx_16950_primary PRIMARY KEY (id);


--
-- Name: pt_channel idx_16954_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel
    ADD CONSTRAINT idx_16954_primary PRIMARY KEY (id);


--
-- Name: pt_channel_content idx_16963_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_content
    ADD CONSTRAINT idx_16963_primary PRIMARY KEY (id);


--
-- Name: pt_channel_subscribe idx_16970_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_subscribe
    ADD CONSTRAINT idx_16970_primary PRIMARY KEY (id);


--
-- Name: pt_config idx_16977_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_config
    ADD CONSTRAINT idx_16977_primary PRIMARY KEY (id);


--
-- Name: pt_login_config idx_16986_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_login_config
    ADD CONSTRAINT idx_16986_primary PRIMARY KEY (id);


--
-- Name: pt_tags idx_16993_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT idx_16993_primary PRIMARY KEY (id);


--
-- Name: pt_view_subject idx_17002_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_view_subject
    ADD CONSTRAINT idx_17002_primary PRIMARY KEY (id);


--
-- Name: schema_version idx_17009_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.schema_version
    ADD CONSTRAINT idx_17009_primary PRIMARY KEY (id);


--
-- Name: sys_business idx_17018_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_business
    ADD CONSTRAINT idx_17018_primary PRIMARY KEY (id);


--
-- Name: sys_file idx_17028_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file
    ADD CONSTRAINT idx_17028_primary PRIMARY KEY (id);


--
-- Name: sys_file_caption idx_17038_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file_caption
    ADD CONSTRAINT idx_17038_primary PRIMARY KEY (id);


--
-- Name: sys_menu idx_17049_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_menu
    ADD CONSTRAINT idx_17049_primary PRIMARY KEY (id);


--
-- Name: sys_module idx_17057_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_module
    ADD CONSTRAINT idx_17057_primary PRIMARY KEY (id);


--
-- Name: sys_permission idx_17067_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_permission
    ADD CONSTRAINT idx_17067_primary PRIMARY KEY (id);


--
-- Name: sys_role idx_17075_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_role
    ADD CONSTRAINT idx_17075_primary PRIMARY KEY (id);


--
-- Name: sys_role_menu idx_17088_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_role_menu
    ADD CONSTRAINT idx_17088_primary PRIMARY KEY (id);


--
-- Name: sys_system idx_17096_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_system
    ADD CONSTRAINT idx_17096_primary PRIMARY KEY (id);


--
-- Name: sys_user idx_17106_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user
    ADD CONSTRAINT idx_17106_primary PRIMARY KEY (id);


--
-- Name: sys_user_role idx_17118_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user_role
    ADD CONSTRAINT idx_17118_primary PRIMARY KEY (id);


--
-- Name: sys_user_weapp idx_17123_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_user_weapp
    ADD CONSTRAINT idx_17123_primary PRIMARY KEY (id);


--
-- Name: sys_weapp idx_17132_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_weapp
    ADD CONSTRAINT idx_17132_primary PRIMARY KEY (id);


--
-- Name: sys_weixin_pay idx_17142_primary; Type: CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_weixin_pay
    ADD CONSTRAINT idx_17142_primary PRIMARY KEY (id);


--
-- Name: idx_16402_feature_toggle_gc_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16402_feature_toggle_gc_master_id_fk ON arena_hub.feature_toggle USING btree (master_id);


--
-- Name: idx_16402_feature_toggle_name_master_id_uindex; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16402_feature_toggle_name_master_id_uindex ON arena_hub.feature_toggle USING btree (name, master_id);


--
-- Name: idx_16411_access_admin_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16411_access_admin_id ON arena_hub.gc_access USING btree (admin_id);


--
-- Name: idx_16411_access_package_img_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16411_access_package_img_id ON arena_hub.gc_access USING btree (package_img_id);


--
-- Name: idx_16411_access_package_video_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16411_access_package_video_file_id ON arena_hub.gc_access USING btree (package_video_file_id);


--
-- Name: idx_16411_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16411_unique ON arena_hub.gc_access USING btree (master_id, code);


--
-- Name: idx_16420_category_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16420_category_file_id ON arena_hub.gc_category USING btree (file_name);


--
-- Name: idx_16426_list_chat_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16426_list_chat_id ON arena_hub.gc_chat_history USING btree (list_chat_id);


--
-- Name: idx_16435_chat_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16435_chat_id ON arena_hub.gc_chat_list USING btree (chat_id);


--
-- Name: idx_16435_chat_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16435_chat_master_id ON arena_hub.gc_chat_list USING btree (master_id);


--
-- Name: idx_16435_chat_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16435_chat_user_id ON arena_hub.gc_chat_list USING btree (user_id);


--
-- Name: idx_16435_chat_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16435_chat_video_id ON arena_hub.gc_chat_list USING btree (video_id);


--
-- Name: idx_16444_gc_content_group_course_assignment_gc_content_group_i; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16444_gc_content_group_course_assignment_gc_content_group_i ON arena_hub.gc_content_group_course_assignment USING btree (content_group_id);


--
-- Name: idx_16444_gc_content_group_course_assignment_gc_created_by_user; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16444_gc_content_group_course_assignment_gc_created_by_user ON arena_hub.gc_content_group_course_assignment USING btree (created_by_user_id);


--
-- Name: idx_16444_gc_content_group_course_assignment_gc_subject_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16444_gc_content_group_course_assignment_gc_subject_id_fk ON arena_hub.gc_content_group_course_assignment USING btree (course_id);


--
-- Name: idx_16452_parent_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16452_parent_id ON arena_hub.gc_course_cate USING btree (parent_id);


--
-- Name: idx_16459_gc_course_complete_read_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16459_gc_course_complete_read_subject_id ON arena_hub.gc_course_complete_read USING btree (subject_id);


--
-- Name: idx_16459_gc_course_complete_read_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16459_gc_course_complete_read_user_id ON arena_hub.gc_course_complete_read USING btree (user_id);


--
-- Name: idx_16466_gc_event_future_pre_event_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16466_gc_event_future_pre_event_id ON arena_hub.gc_event USING btree (future_pre_event_id);


--
-- Name: idx_16466_gc_event_link_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16466_gc_event_link_file_id ON arena_hub.gc_event USING btree (link_file_id);


--
-- Name: idx_16466_gc_event_link_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16466_gc_event_link_video_id ON arena_hub.gc_event USING btree (link_video_id);


--
-- Name: idx_16466_video_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16466_video_id_fk ON arena_hub.gc_event USING btree (video_id);


--
-- Name: idx_16478_gc_faq_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16478_gc_faq_id ON arena_hub.gc_faq USING btree (faq_id);


--
-- Name: idx_16488_fb_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16488_fb_user_fk ON arena_hub.gc_feed_back USING btree (user_id);


--
-- Name: idx_16497_g_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16497_g_master_id_fk ON arena_hub.gc_group USING btree (master_id);


--
-- Name: idx_16497_group_access_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16497_group_access_id ON arena_hub.gc_group USING btree (access_id);


--
-- Name: idx_16497_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16497_unique ON arena_hub.gc_group USING btree (user_access_id, code);


--
-- Name: idx_16507_gc_mentor_group; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16507_gc_mentor_group ON arena_hub.gc_group_mentor USING btree (group_id, user_access_id);


--
-- Name: idx_16507_gc_mentor_user_access_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16507_gc_mentor_user_access_id ON arena_hub.gc_group_mentor USING btree (user_access_id);


--
-- Name: idx_16512_manager_master_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16512_manager_master_fk ON arena_hub.gc_manager USING btree (master_id);


--
-- Name: idx_16512_unique_username; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16512_unique_username ON arena_hub.gc_manager USING btree (username, sys_id);


--
-- Name: idx_16533_manager_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16533_manager_id ON arena_hub.gc_master USING btree (manager_id);


--
-- Name: idx_16533_unique_context; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16533_unique_context ON arena_hub.gc_master USING btree (context);


--
-- Name: idx_16547_active_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16547_active_video_id ON arena_hub.gc_master_active USING btree (video_id);


--
-- Name: idx_16547_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16547_master_id ON arena_hub.gc_master_active USING btree (master_id);


--
-- Name: idx_16554_home_info_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16554_home_info_master_id ON arena_hub.gc_master_home_info USING btree (master_id);


--
-- Name: idx_16554_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16554_master_id ON arena_hub.gc_master_home_info USING btree (master_id, name);


--
-- Name: idx_16564_log_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16564_log_master_id_fk ON arena_hub.gc_master_log USING btree (master_id);


--
-- Name: idx_16573_master_message_res_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_master_message_res_id ON arena_hub.gc_master_message USING btree (res_id);


--
-- Name: idx_16573_master_message_target_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_master_message_target_user_id ON arena_hub.gc_master_message USING btree (target_user_id);


--
-- Name: idx_16573_master_message_user_answer_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_master_message_user_answer_id_fk ON arena_hub.gc_master_message USING btree (user_answer_id);


--
-- Name: idx_16573_master_message_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_master_message_user_id ON arena_hub.gc_master_message USING btree (user_id);


--
-- Name: idx_16573_master_message_user_schedule_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_master_message_user_schedule_id_fk ON arena_hub.gc_master_message USING btree (user_schedule_id);


--
-- Name: idx_16573_message_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16573_message_master_id_fk ON arena_hub.gc_master_message USING btree (master_id);


--
-- Name: idx_16583_record_portal_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16583_record_portal_id ON arena_hub.gc_master_pay_records USING btree (master_id);


--
-- Name: idx_16583_record_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16583_record_subject_id ON arena_hub.gc_master_pay_records USING btree (subject_id);


--
-- Name: idx_16583_record_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16583_record_user_id ON arena_hub.gc_master_pay_records USING btree (user_id);


--
-- Name: idx_16592_record_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16592_record_subject_id ON arena_hub.gc_master_sales_page USING btree (subject_id);


--
-- Name: idx_16592_section_type_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16592_section_type_id ON arena_hub.gc_master_sales_page USING btree (section_type_id);


--
-- Name: idx_16601_record_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16601_record_subject_id ON arena_hub.gc_master_section_type USING btree (section_name);


--
-- Name: idx_16610_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16610_master_id ON arena_hub.gc_paypal_info USING btree (master_id);


--
-- Name: idx_16629_res_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16629_res_video_id ON arena_hub.gc_resource USING btree (video_id);


--
-- Name: idx_16640_social_media_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16640_social_media_master_id ON arena_hub.gc_social_media USING btree (master_id);


--
-- Name: idx_16649_fk_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_fk_user_id ON arena_hub.gc_subject USING btree (create_user);


--
-- Name: idx_16649_gc_subject_alias_sub_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_gc_subject_alias_sub_id ON arena_hub.gc_subject USING btree (alias_sub_id);


--
-- Name: idx_16649_gc_subject_fid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_gc_subject_fid ON arena_hub.gc_subject USING btree (fid);


--
-- Name: idx_16649_gc_subject_sub_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_gc_subject_sub_id ON arena_hub.gc_subject USING btree (sub_id);


--
-- Name: idx_16649_sub_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_sub_master_id ON arena_hub.gc_subject USING btree (master_id);


--
-- Name: idx_16649_subject_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16649_subject_file_id ON arena_hub.gc_subject USING btree (sub_img_id);


--
-- Name: idx_16649_token; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16649_token ON arena_hub.gc_subject USING btree (token);


--
-- Name: idx_16663_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16663_master_id ON arena_hub.gc_subject_association USING btree (master_id, subject_id);


--
-- Name: idx_16663_sub_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16663_sub_master_id ON arena_hub.gc_subject_association USING btree (master_id);


--
-- Name: idx_16663_subject_import_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16663_subject_import_subject_id ON arena_hub.gc_subject_association USING btree (subject_id);


--
-- Name: idx_16677_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16677_subject_id ON arena_hub.gc_subject_intro_info USING btree (subject_id, name);


--
-- Name: idx_16677_subject_intro_info_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16677_subject_intro_info_file_id ON arena_hub.gc_subject_intro_info USING btree (file_id);


--
-- Name: idx_16677_subject_intro_info_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16677_subject_intro_info_subject_id ON arena_hub.gc_subject_intro_info USING btree (subject_id);


--
-- Name: idx_16686_fk_association_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16686_fk_association_master_id ON arena_hub.gc_subject_tag_association USING btree (master_id);


--
-- Name: idx_16686_package_status; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16686_package_status ON arena_hub.gc_subject_tag_association USING btree (package_status, master_id, tag_id);


--
-- Name: idx_16686_unique_tag; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16686_unique_tag ON arena_hub.gc_subject_tag_association USING btree (tag_id, master_id);


--
-- Name: idx_16695_gc_tags_subid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16695_gc_tags_subid ON arena_hub.gc_subject_tags USING btree (subject_id);


--
-- Name: idx_16695_unique_tag; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16695_unique_tag ON arena_hub.gc_subject_tags USING btree (master_id, subject_id, tag_text);


--
-- Name: idx_16704_sysid_username_unque; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16704_sysid_username_unque ON arena_hub.gc_user USING btree (sys_id, username);


--
-- Name: idx_16704_user_id_pid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16704_user_id_pid ON arena_hub.gc_user USING btree (pid);


--
-- Name: idx_16704_user_ui_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16704_user_ui_fk ON arena_hub.gc_user USING btree (info_id);


--
-- Name: idx_16716_ua_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16716_ua_access_fk ON arena_hub.gc_user_access USING btree (access_id);


--
-- Name: idx_16716_ua_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16716_ua_master_id_fk ON arena_hub.gc_user_access USING btree (master_id);


--
-- Name: idx_16716_ua_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16716_ua_user_fk ON arena_hub.gc_user_access USING btree (user_id);


--
-- Name: idx_16716_user_access_id_master_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16716_user_access_id_master_id_fk ON arena_hub.gc_user_access USING btree (master_id, user_id, access_id);


--
-- Name: idx_16716_user_accrss_manager_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16716_user_accrss_manager_fk ON arena_hub.gc_user_access USING btree (manager_id);


--
-- Name: idx_16726_uaa_user_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16726_uaa_user_access_fk ON arena_hub.gc_user_access_active USING btree (user_access_id);


--
-- Name: idx_16726_uaa_vid_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16726_uaa_vid_fk ON arena_hub.gc_user_access_active USING btree (vid);


--
-- Name: idx_16733_uae_user_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16733_uae_user_access_fk ON arena_hub.gc_user_access_ext USING btree (user_access_id);


--
-- Name: idx_16733_uae_user_access_managerid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16733_uae_user_access_managerid ON arena_hub.gc_user_access_ext USING btree (manager_id);


--
-- Name: idx_16739_invite_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16739_invite_unique ON arena_hub.gc_user_access_invite USING btree (user_access_id, target_access_id);


--
-- Name: idx_16739_uai_t_user_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16739_uai_t_user_access_fk ON arena_hub.gc_user_access_invite USING btree (target_access_id);


--
-- Name: idx_16746_ual_user_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16746_ual_user_access_fk ON arena_hub.gc_user_access_log USING btree (user_access_id);


--
-- Name: idx_16755_user_access_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16755_user_access_id ON arena_hub.gc_user_access_permission USING btree (user_access_id);


--
-- Name: idx_16764_uas_user_access_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16764_uas_user_access_fk ON arena_hub.gc_user_access_share USING btree (user_access_id);


--
-- Name: idx_16774_uan_master_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16774_uan_master_fk ON arena_hub.gc_user_answer USING btree (master_id);


--
-- Name: idx_16774_uan_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16774_uan_user_fk ON arena_hub.gc_user_answer USING btree (user_id);


--
-- Name: idx_16774_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16774_unique ON arena_hub.gc_user_answer USING btree (event_id, user_id, master_id);


--
-- Name: idx_16783_ue_event_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16783_ue_event_id_fk ON arena_hub.gc_user_event USING btree (event_id);


--
-- Name: idx_16783_ue_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16783_ue_master_id ON arena_hub.gc_user_event USING btree (master_id);


--
-- Name: idx_16783_ue_user_id_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16783_ue_user_id_fk ON arena_hub.gc_user_event USING btree (user_id);


--
-- Name: idx_16783_userid_eventid_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16783_userid_eventid_unique ON arena_hub.gc_user_event USING btree (user_id, event_id);


--
-- Name: idx_16791_uer_event_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_uer_event_fk ON arena_hub.gc_user_event_resource USING btree (event_id);


--
-- Name: idx_16791_uer_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_uer_user_fk ON arena_hub.gc_user_event_resource USING btree (user_id);


--
-- Name: idx_16791_user_event_resource_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_user_event_resource_file_id ON arena_hub.gc_user_event_resource USING btree (file_id);


--
-- Name: idx_16791_user_event_resource_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_user_event_resource_master_id ON arena_hub.gc_user_event_resource USING btree (master_id);


--
-- Name: idx_16791_user_event_resource_target_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_user_event_resource_target_id ON arena_hub.gc_user_event_resource USING btree (target_id);


--
-- Name: idx_16791_user_event_resource_target_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16791_user_event_resource_target_user_id ON arena_hub.gc_user_event_resource USING btree (target_user_id);


--
-- Name: idx_16801_uf_event_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16801_uf_event_id ON arena_hub.gc_user_fabulous USING btree (event_id);


--
-- Name: idx_16801_uf_target_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16801_uf_target_user_id ON arena_hub.gc_user_fabulous USING btree (target_user_id);


--
-- Name: idx_16801_uf_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16801_uf_user_id ON arena_hub.gc_user_fabulous USING btree (user_id);


--
-- Name: idx_16801_uf_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16801_uf_video_id ON arena_hub.gc_user_fabulous USING btree (video_id);


--
-- Name: idx_16808_info_avatar_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16808_info_avatar_file_id ON arena_hub.gc_user_info USING btree (avatar_file_id);


--
-- Name: idx_16817_message_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16817_message_file_id ON arena_hub.gc_user_message USING btree (file_id);


--
-- Name: idx_16817_uum_master_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16817_uum_master_fk ON arena_hub.gc_user_message USING btree (master_id);


--
-- Name: idx_16817_uum_target_user; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16817_uum_target_user ON arena_hub.gc_user_message USING btree (target_user_id);


--
-- Name: idx_16817_uum_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16817_uum_user_fk ON arena_hub.gc_user_message USING btree (user_id);


--
-- Name: idx_16826_note_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16826_note_file_id ON arena_hub.gc_user_note USING btree (file_id);


--
-- Name: idx_16826_un_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16826_un_master_id ON arena_hub.gc_user_note USING btree (master_id);


--
-- Name: idx_16826_un_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16826_un_user_fk ON arena_hub.gc_user_note USING btree (user_id);


--
-- Name: idx_16826_un_video_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16826_un_video_fk ON arena_hub.gc_user_note USING btree (video_id);


--
-- Name: idx_16835_unc_event_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16835_unc_event_id ON arena_hub.gc_user_note_comment USING btree (event_id);


--
-- Name: idx_16835_unc_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16835_unc_file_id ON arena_hub.gc_user_note_comment USING btree (file_id);


--
-- Name: idx_16835_unc_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16835_unc_master_id ON arena_hub.gc_user_note_comment USING btree (master_id);


--
-- Name: idx_16835_unc_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16835_unc_user_fk ON arena_hub.gc_user_note_comment USING btree (user_id);


--
-- Name: idx_16835_user_node_comment_target_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16835_user_node_comment_target_user_id ON arena_hub.gc_user_note_comment USING btree (target_user_id);


--
-- Name: idx_16844_usc_folder_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16844_usc_folder_fk ON arena_hub.gc_user_save_content USING btree (folder_id);


--
-- Name: idx_16844_usc_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16844_usc_master_id ON arena_hub.gc_user_save_content USING btree (master_id);


--
-- Name: idx_16844_usc_subject_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16844_usc_subject_fk ON arena_hub.gc_user_save_content USING btree (sub_id);


--
-- Name: idx_16844_usc_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16844_usc_user_fk ON arena_hub.gc_user_save_content USING btree (user_id);


--
-- Name: idx_16844_usc_video_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16844_usc_video_fk ON arena_hub.gc_user_save_content USING btree (video_id);


--
-- Name: idx_16851_gc_user_save_content_follow_folderid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16851_gc_user_save_content_follow_folderid ON arena_hub.gc_user_save_content_follow USING btree (folder_id);


--
-- Name: idx_16851_gc_user_save_content_follow_uid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16851_gc_user_save_content_follow_uid ON arena_hub.gc_user_save_content_follow USING btree (user_id);


--
-- Name: idx_16858_usf_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16858_usf_file_id ON arena_hub.gc_user_save_folder USING btree (file_id);


--
-- Name: idx_16858_usf_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16858_usf_master_id ON arena_hub.gc_user_save_folder USING btree (master_id);


--
-- Name: idx_16858_usf_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16858_usf_user_fk ON arena_hub.gc_user_save_folder USING btree (user_id);


--
-- Name: idx_16867_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16867_unique ON arena_hub.gc_user_schedule USING btree (user_id, master_id, sub_id);


--
-- Name: idx_16867_us_addby_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16867_us_addby_user_id ON arena_hub.gc_user_schedule USING btree (addby_user_id);


--
-- Name: idx_16867_us_sub_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16867_us_sub_fk ON arena_hub.gc_user_schedule USING btree (sub_id);


--
-- Name: idx_16867_us_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16867_us_video_id ON arena_hub.gc_user_schedule USING btree (video_id);


--
-- Name: idx_16867_user_schedule_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16867_user_schedule_master_id ON arena_hub.gc_user_schedule USING btree (master_id);


--
-- Name: idx_16876_gc_user_stripe_uid_customer_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16876_gc_user_stripe_uid_customer_id ON arena_hub.gc_user_stripe USING btree (stripe_customer_id, user_id);


--
-- Name: idx_16876_uid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16876_uid ON arena_hub.gc_user_stripe USING btree (user_id);


--
-- Name: idx_16885_stripe_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16885_stripe_user_id ON arena_hub.gc_user_stripe_subscription USING btree (user_stripe_id);


--
-- Name: idx_16885_subscription_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16885_subscription_master_id ON arena_hub.gc_user_stripe_subscription USING btree (master_id);


--
-- Name: idx_16894_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16894_unique ON arena_hub.gc_user_video_action USING btree (video_id, user_id, type);


--
-- Name: idx_16894_uva_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16894_uva_file_id ON arena_hub.gc_user_video_action USING btree (file_id);


--
-- Name: idx_16894_uva_sub_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16894_uva_sub_id ON arena_hub.gc_user_video_action USING btree (sub_id);


--
-- Name: idx_16894_uva_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16894_uva_user_fk ON arena_hub.gc_user_video_action USING btree (user_id);


--
-- Name: idx_16903_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16903_file_id ON arena_hub.gc_user_video_play USING btree (file_id);


--
-- Name: idx_16903_uvp_master_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16903_uvp_master_fk ON arena_hub.gc_user_video_play USING btree (master_id);


--
-- Name: idx_16903_uvp_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16903_uvp_user_fk ON arena_hub.gc_user_video_play USING btree (user_id);


--
-- Name: idx_16903_uvp_video_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16903_uvp_video_fk ON arena_hub.gc_user_video_play USING btree (video_id);


--
-- Name: idx_16910_vpr_uservideoplay_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16910_vpr_uservideoplay_fk ON arena_hub.gc_user_video_plays_node USING btree (videoplay_id);


--
-- Name: idx_16918_openid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16918_openid ON arena_hub.gc_user_weapp USING btree (openid);


--
-- Name: idx_16918_uid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16918_uid ON arena_hub.gc_user_weapp USING btree (uid);


--
-- Name: idx_16927_gc_transcript_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16927_gc_transcript_file_id ON arena_hub.gc_video USING btree (transcript_file_id);


--
-- Name: idx_16927_gc_video_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16927_gc_video_file_id ON arena_hub.gc_video USING btree (file_id);


--
-- Name: idx_16942_comment_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_comment_master_id ON arena_hub.gc_video_comment USING btree (master_id);


--
-- Name: idx_16942_vc_user_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_vc_user_fk ON arena_hub.gc_video_comment USING btree (user_id);


--
-- Name: idx_16942_vc_video_fk; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_vc_video_fk ON arena_hub.gc_video_comment USING btree (video_id);


--
-- Name: idx_16942_video_comment_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_video_comment_file_id ON arena_hub.gc_video_comment USING btree (file_id);


--
-- Name: idx_16942_video_main_comment_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_video_main_comment_id ON arena_hub.gc_video_comment USING btree (main_comment_id);


--
-- Name: idx_16942_video_reply_comment_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_video_reply_comment_id ON arena_hub.gc_video_comment USING btree (reply_comment_id);


--
-- Name: idx_16942_video_target_user_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16942_video_target_user_id ON arena_hub.gc_video_comment USING btree (target_user_id);


--
-- Name: idx_16954_unique_channel_slug; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16954_unique_channel_slug ON arena_hub.pt_channel USING btree (master_id, channel_slug);


--
-- Name: idx_16963_channel_fileid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16963_channel_fileid ON arena_hub.pt_channel_content USING btree (file_id);


--
-- Name: idx_16963_channel_video_section; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16963_channel_video_section ON arena_hub.pt_channel_content USING btree (section_id);


--
-- Name: idx_16963_channel_video_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16963_channel_video_unique ON arena_hub.pt_channel_content USING btree (channel_id, file_id);


--
-- Name: idx_16970_channel_subscribe; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16970_channel_subscribe ON arena_hub.pt_channel_subscribe USING btree (channel_id);


--
-- Name: idx_16970_unique_user_channel; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_16970_unique_user_channel ON arena_hub.pt_channel_subscribe USING btree (user_id, channel_id);


--
-- Name: idx_16986_gc_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16986_gc_master_id ON arena_hub.pt_login_config USING btree (master_id);


--
-- Name: idx_16993_channel_file_tag_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16993_channel_file_tag_id ON arena_hub.pt_tags USING btree (file_id);


--
-- Name: idx_16993_fk_channel_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16993_fk_channel_id ON arena_hub.pt_tags USING btree (channel_id);


--
-- Name: idx_16993_fk_resource_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16993_fk_resource_id ON arena_hub.pt_tags USING btree (resource_id);


--
-- Name: idx_16993_fk_subject_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16993_fk_subject_id ON arena_hub.pt_tags USING btree (subject_id);


--
-- Name: idx_16993_fk_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_16993_fk_video_id ON arena_hub.pt_tags USING btree (video_id);


--
-- Name: idx_17002_fk_master_id_pt; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17002_fk_master_id_pt ON arena_hub.pt_view_subject USING btree (master_id);


--
-- Name: idx_17002_fk_subject_id_pt; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17002_fk_subject_id_pt ON arena_hub.pt_view_subject USING btree (subject_id);


--
-- Name: idx_17002_fk_user_id_pt; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17002_fk_user_id_pt ON arena_hub.pt_view_subject USING btree (user_id);


--
-- Name: idx_17018_key; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17018_key ON arena_hub.sys_business USING btree (key);


--
-- Name: idx_17028_fk_thumb_nail_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17028_fk_thumb_nail_id ON arena_hub.sys_file USING btree (thumb_nail_id);


--
-- Name: idx_17028_sys_file_master_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17028_sys_file_master_id ON arena_hub.sys_file USING btree (master_id);


--
-- Name: idx_17038_caption_file_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17038_caption_file_id ON arena_hub.sys_file_caption USING btree (caption_file_id);


--
-- Name: idx_17038_video_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17038_video_id ON arena_hub.sys_file_caption USING btree (video_id);


--
-- Name: idx_17067_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17067_unique ON arena_hub.sys_permission USING btree (role_id, module_id, perm_code);


--
-- Name: idx_17075_unique_key; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17075_unique_key ON arena_hub.sys_role USING btree (role_key, sys_id);


--
-- Name: idx_17096_name_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE INDEX idx_17096_name_unique ON arena_hub.sys_system USING btree (business_id, name);


--
-- Name: idx_17106_username_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17106_username_unique ON arena_hub.sys_user USING btree (username, sys_id);


--
-- Name: idx_17118_unique; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17118_unique ON arena_hub.sys_user_role USING btree (user_id, role_id);


--
-- Name: idx_17123_openid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17123_openid ON arena_hub.sys_user_weapp USING btree (openid);


--
-- Name: idx_17123_uid; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17123_uid ON arena_hub.sys_user_weapp USING btree (uid);


--
-- Name: idx_17132_sys_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17132_sys_id ON arena_hub.sys_weapp USING btree (sys_id);


--
-- Name: idx_17142_order_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17142_order_id ON arena_hub.sys_weixin_pay USING btree (order_id);


--
-- Name: idx_17142_prepay_id; Type: INDEX; Schema: arena_hub; Owner: postgres
--

CREATE UNIQUE INDEX idx_17142_prepay_id ON arena_hub.sys_weixin_pay USING btree (prepay_id);


--
-- Name: gc_course_cate on_update_current_timestamp; Type: TRIGGER; Schema: arena_hub; Owner: postgres
--

CREATE TRIGGER on_update_current_timestamp BEFORE UPDATE ON arena_hub.gc_course_cate FOR EACH ROW EXECUTE FUNCTION arena_hub.on_update_current_timestamp_gc_course_cate();


--
-- Name: gc_subject_complete on_update_current_timestamp; Type: TRIGGER; Schema: arena_hub; Owner: postgres
--

CREATE TRIGGER on_update_current_timestamp BEFORE UPDATE ON arena_hub.gc_subject_complete FOR EACH ROW EXECUTE FUNCTION arena_hub.on_update_current_timestamp_gc_subject_complete();


--
-- Name: sys_menu on_update_current_timestamp; Type: TRIGGER; Schema: arena_hub; Owner: postgres
--

CREATE TRIGGER on_update_current_timestamp BEFORE UPDATE ON arena_hub.sys_menu FOR EACH ROW EXECUTE FUNCTION arena_hub.on_update_current_timestamp_sys_menu();


--
-- Name: sys_role_menu on_update_current_timestamp; Type: TRIGGER; Schema: arena_hub; Owner: postgres
--

CREATE TRIGGER on_update_current_timestamp BEFORE UPDATE ON arena_hub.sys_role_menu FOR EACH ROW EXECUTE FUNCTION arena_hub.on_update_current_timestamp_sys_role_menu();


--
-- Name: gc_access access_admin_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access
    ADD CONSTRAINT access_admin_id FOREIGN KEY (admin_id) REFERENCES arena_hub.gc_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_access access_package_img_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access
    ADD CONSTRAINT access_package_img_id FOREIGN KEY (package_img_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_access access_package_video_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access
    ADD CONSTRAINT access_package_video_file_id FOREIGN KEY (package_video_file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_active active_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_active
    ADD CONSTRAINT active_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sys_file_caption caption_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file_caption
    ADD CONSTRAINT caption_file_id FOREIGN KEY (caption_file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_tags channel_file_tag_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT channel_file_tag_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_channel_content channel_fileid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_content
    ADD CONSTRAINT channel_fileid FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_channel_subscribe channel_subscribe; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_subscribe
    ADD CONSTRAINT channel_subscribe FOREIGN KEY (channel_id) REFERENCES arena_hub.pt_channel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_channel_subscribe channel_subscribe_user; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_subscribe
    ADD CONSTRAINT channel_subscribe_user FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_channel_content channel_video_section; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_content
    ADD CONSTRAINT channel_video_section FOREIGN KEY (section_id) REFERENCES arena_hub.pt_channel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_chat_list chat_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_list
    ADD CONSTRAINT chat_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_chat_list chat_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_list
    ADD CONSTRAINT chat_user_id FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_chat_list chat_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_chat_list
    ADD CONSTRAINT chat_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment comment_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT comment_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_feed_back fb_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_feed_back
    ADD CONSTRAINT fb_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: feature_toggle feature_toggle_gc_master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.feature_toggle
    ADD CONSTRAINT feature_toggle_gc_master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_tag_association fk_association_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tag_association
    ADD CONSTRAINT fk_association_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_tag_association fk_association_tag_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tag_association
    ADD CONSTRAINT fk_association_tag_id FOREIGN KEY (tag_id) REFERENCES arena_hub.gc_subject_tags(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_tags fk_channel_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT fk_channel_id FOREIGN KEY (channel_id) REFERENCES arena_hub.pt_channel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_view_subject fk_master_id_pt; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_view_subject
    ADD CONSTRAINT fk_master_id_pt FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_tags fk_resource_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT fk_resource_id FOREIGN KEY (resource_id) REFERENCES arena_hub.gc_resource(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_tags fk_subject_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT fk_subject_id FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_view_subject fk_subject_id_pt; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_view_subject
    ADD CONSTRAINT fk_subject_id_pt FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sys_file fk_thumb_nail_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file
    ADD CONSTRAINT fk_thumb_nail_id FOREIGN KEY (thumb_nail_id) REFERENCES arena_hub.sys_file(id) ON UPDATE SET NULL ON DELETE SET NULL;


--
-- Name: gc_subject fk_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT fk_user_id FOREIGN KEY (create_user) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_view_subject fk_user_id_pt; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_view_subject
    ADD CONSTRAINT fk_user_id_pt FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_tags fk_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_tags
    ADD CONSTRAINT fk_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_group g_master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group
    ADD CONSTRAINT g_master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_content_group_course_assignment gc_content_group_course_assignment_gc_content_group_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_content_group_course_assignment
    ADD CONSTRAINT gc_content_group_course_assignment_gc_content_group_id_fk FOREIGN KEY (content_group_id) REFERENCES arena_hub.gc_access(id) ON UPDATE CASCADE;


--
-- Name: gc_content_group_course_assignment gc_content_group_course_assignment_gc_created_by_user_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_content_group_course_assignment
    ADD CONSTRAINT gc_content_group_course_assignment_gc_created_by_user_id_fk FOREIGN KEY (created_by_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE;


--
-- Name: gc_content_group_course_assignment gc_content_group_course_assignment_gc_subject_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_content_group_course_assignment
    ADD CONSTRAINT gc_content_group_course_assignment_gc_subject_id_fk FOREIGN KEY (course_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_course_complete_read gc_course_complete_read_subject_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_complete_read
    ADD CONSTRAINT gc_course_complete_read_subject_id FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_course_complete_read gc_course_complete_read_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_course_complete_read
    ADD CONSTRAINT gc_course_complete_read_user_id FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_event gc_event_future_pre_event_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event
    ADD CONSTRAINT gc_event_future_pre_event_id FOREIGN KEY (future_pre_event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_event gc_event_link_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event
    ADD CONSTRAINT gc_event_link_file_id FOREIGN KEY (link_file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_event gc_event_link_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event
    ADD CONSTRAINT gc_event_link_video_id FOREIGN KEY (link_video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_faq gc_faq_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_faq
    ADD CONSTRAINT gc_faq_id FOREIGN KEY (faq_id) REFERENCES arena_hub.gc_faq(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_login_config gc_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_login_config
    ADD CONSTRAINT gc_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_sales_page gc_master_sales_page_ibfk_2; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_sales_page
    ADD CONSTRAINT gc_master_sales_page_ibfk_2 FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_group_mentor gc_mentor_group_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group_mentor
    ADD CONSTRAINT gc_mentor_group_id FOREIGN KEY (group_id) REFERENCES arena_hub.gc_group(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_group_mentor gc_mentor_user_access_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group_mentor
    ADD CONSTRAINT gc_mentor_user_access_id FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject gc_subject_alias_sub_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT gc_subject_alias_sub_id FOREIGN KEY (alias_sub_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject gc_subject_fid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT gc_subject_fid FOREIGN KEY (fid) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject gc_subject_sub_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT gc_subject_sub_id FOREIGN KEY (sub_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_tags gc_tags_masterid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tags
    ADD CONSTRAINT gc_tags_masterid FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_tags gc_tags_subid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_tags
    ADD CONSTRAINT gc_tags_subid FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video gc_transcript_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video
    ADD CONSTRAINT gc_transcript_file_id FOREIGN KEY (transcript_file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content_follow gc_user_save_content_follow_folderid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content_follow
    ADD CONSTRAINT gc_user_save_content_follow_folderid FOREIGN KEY (folder_id) REFERENCES arena_hub.gc_user_save_folder(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content_follow gc_user_save_content_follow_uid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content_follow
    ADD CONSTRAINT gc_user_save_content_follow_uid FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_play gc_user_video_play_ibfk_1; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT gc_user_video_play_ibfk_1 FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_play gc_user_video_play_ibfk_2; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT gc_user_video_play_ibfk_2 FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video gc_video_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video
    ADD CONSTRAINT gc_video_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_group group_access_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group
    ADD CONSTRAINT group_access_id FOREIGN KEY (access_id) REFERENCES arena_hub.gc_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_home_info home_info_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_home_info
    ADD CONSTRAINT home_info_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_info info_avatar_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_info
    ADD CONSTRAINT info_avatar_file_id FOREIGN KEY (avatar_file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_log log_master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_log
    ADD CONSTRAINT log_master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_manager manager_master_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_manager
    ADD CONSTRAINT manager_master_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_active master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_active
    ADD CONSTRAINT master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_access master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_access
    ADD CONSTRAINT master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message master_message_res_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT master_message_res_id FOREIGN KEY (res_id) REFERENCES arena_hub.gc_user_event_resource(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message master_message_target_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT master_message_target_user_id FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message master_message_user_answer_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT master_message_user_answer_id_fk FOREIGN KEY (user_answer_id) REFERENCES arena_hub.gc_user_answer(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message master_message_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT master_message_user_id FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message master_message_user_schedule_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT master_message_user_schedule_id_fk FOREIGN KEY (user_schedule_id) REFERENCES arena_hub.gc_user_schedule(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_message message_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message
    ADD CONSTRAINT message_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_message message_master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_message
    ADD CONSTRAINT message_master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note note_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note
    ADD CONSTRAINT note_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_paypal_info paypal_master; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_paypal_info
    ADD CONSTRAINT paypal_master FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: pt_channel_content pt_channel_content_ibfk_1; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.pt_channel_content
    ADD CONSTRAINT pt_channel_content_ibfk_1 FOREIGN KEY (channel_id) REFERENCES arena_hub.pt_channel(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_pay_records record_portal_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_pay_records
    ADD CONSTRAINT record_portal_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_pay_records record_subject_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_pay_records
    ADD CONSTRAINT record_subject_id FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_pay_records record_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_pay_records
    ADD CONSTRAINT record_user_id FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_resource res_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_resource
    ADD CONSTRAINT res_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_master_sales_page section_type_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_master_sales_page
    ADD CONSTRAINT section_type_id FOREIGN KEY (section_type_id) REFERENCES arena_hub.gc_master_section_type(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_social_media social_media_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_social_media
    ADD CONSTRAINT social_media_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_stripe_subscription stripe_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe_subscription
    ADD CONSTRAINT stripe_user_id FOREIGN KEY (user_stripe_id) REFERENCES arena_hub.gc_user_stripe(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject sub_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT sub_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject subject_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject
    ADD CONSTRAINT subject_file_id FOREIGN KEY (sub_img_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_association subject_import_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_association
    ADD CONSTRAINT subject_import_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_association subject_import_subject_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_association
    ADD CONSTRAINT subject_import_subject_id FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_intro_info subject_intro_info_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_intro_info
    ADD CONSTRAINT subject_intro_info_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_subject_intro_info subject_intro_info_subject_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_subject_intro_info
    ADD CONSTRAINT subject_intro_info_subject_id FOREIGN KEY (subject_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_stripe_subscription subscription_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe_subscription
    ADD CONSTRAINT subscription_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sys_file sys_file_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file
    ADD CONSTRAINT sys_file_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access ua_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access
    ADD CONSTRAINT ua_access_fk FOREIGN KEY (access_id) REFERENCES arena_hub.gc_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access ua_master_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access
    ADD CONSTRAINT ua_master_id_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access ua_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access
    ADD CONSTRAINT ua_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_active uaa_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_active
    ADD CONSTRAINT uaa_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_active uaa_vid_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_active
    ADD CONSTRAINT uaa_vid_fk FOREIGN KEY (vid) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_ext uae_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_ext
    ADD CONSTRAINT uae_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_ext uae_user_access_managerid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_ext
    ADD CONSTRAINT uae_user_access_managerid FOREIGN KEY (manager_id) REFERENCES arena_hub.gc_manager(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_invite uai_t_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_invite
    ADD CONSTRAINT uai_t_user_access_fk FOREIGN KEY (target_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_invite uai_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_invite
    ADD CONSTRAINT uai_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_log ual_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_log
    ADD CONSTRAINT ual_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_answer uan_event_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_answer
    ADD CONSTRAINT uan_event_fk FOREIGN KEY (event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_answer uan_master_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_answer
    ADD CONSTRAINT uan_master_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_answer uan_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_answer
    ADD CONSTRAINT uan_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_permission uap_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_permission
    ADD CONSTRAINT uap_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access_share uas_user_access_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access_share
    ADD CONSTRAINT uas_user_access_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event ue_event_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event
    ADD CONSTRAINT ue_event_id_fk FOREIGN KEY (event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event ue_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event
    ADD CONSTRAINT ue_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event ue_user_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event
    ADD CONSTRAINT ue_user_id_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: gc_user_event_resource uer_event_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT uer_event_fk FOREIGN KEY (event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event_resource uer_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT uer_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_fabulous uf_event_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous
    ADD CONSTRAINT uf_event_id FOREIGN KEY (event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_fabulous uf_target_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous
    ADD CONSTRAINT uf_target_user_id FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_fabulous uf_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous
    ADD CONSTRAINT uf_user_id FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_fabulous uf_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_fabulous
    ADD CONSTRAINT uf_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_stripe uid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_stripe
    ADD CONSTRAINT uid FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note un_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note
    ADD CONSTRAINT un_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note un_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note
    ADD CONSTRAINT un_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note un_video_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note
    ADD CONSTRAINT un_video_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note_comment unc_event_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT unc_event_id FOREIGN KEY (event_id) REFERENCES arena_hub.gc_event(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note_comment unc_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT unc_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: gc_user_note_comment unc_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT unc_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note_comment unc_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT unc_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_schedule us_addby_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT us_addby_user_id FOREIGN KEY (addby_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: gc_user_schedule us_sub_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT us_sub_fk FOREIGN KEY (sub_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_schedule us_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT us_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: gc_user_schedule us_video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT us_video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content usc_folder_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT usc_folder_fk FOREIGN KEY (folder_id) REFERENCES arena_hub.gc_user_save_folder(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content usc_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT usc_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content usc_subject_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT usc_subject_fk FOREIGN KEY (sub_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content usc_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT usc_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_content usc_video_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_content
    ADD CONSTRAINT usc_video_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_group user_access_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_group
    ADD CONSTRAINT user_access_id_fk FOREIGN KEY (user_access_id) REFERENCES arena_hub.gc_user_access(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_access user_accrss_manager_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_access
    ADD CONSTRAINT user_accrss_manager_fk FOREIGN KEY (manager_id) REFERENCES arena_hub.gc_manager(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event_resource user_event_resource_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT user_event_resource_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event_resource user_event_resource_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT user_event_resource_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event_resource user_event_resource_target_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT user_event_resource_target_id FOREIGN KEY (target_id) REFERENCES arena_hub.gc_user_event_resource(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_event_resource user_event_resource_target_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_event_resource
    ADD CONSTRAINT user_event_resource_target_user_id FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user user_id_pid; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user
    ADD CONSTRAINT user_id_pid FOREIGN KEY (pid) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_note_comment user_node_comment_target_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_note_comment
    ADD CONSTRAINT user_node_comment_target_user_id FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_schedule user_schedule_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_schedule
    ADD CONSTRAINT user_schedule_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: gc_user user_ui_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user
    ADD CONSTRAINT user_ui_fk FOREIGN KEY (info_id) REFERENCES arena_hub.gc_user_info(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_folder usf_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_folder
    ADD CONSTRAINT usf_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_folder usf_master_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_folder
    ADD CONSTRAINT usf_master_id FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_save_folder usf_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_save_folder
    ADD CONSTRAINT usf_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_message uum_master_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message
    ADD CONSTRAINT uum_master_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_message uum_target_user; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message
    ADD CONSTRAINT uum_target_user FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_message uum_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_message
    ADD CONSTRAINT uum_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_action uva_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action
    ADD CONSTRAINT uva_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_action uva_sub_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action
    ADD CONSTRAINT uva_sub_id FOREIGN KEY (sub_id) REFERENCES arena_hub.gc_subject(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_action uva_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action
    ADD CONSTRAINT uva_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_action uva_video_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_action
    ADD CONSTRAINT uva_video_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_play uvp_master_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT uvp_master_fk FOREIGN KEY (master_id) REFERENCES arena_hub.gc_master(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_play uvp_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT uvp_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_play uvp_video_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_play
    ADD CONSTRAINT uvp_video_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment vc_user_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT vc_user_fk FOREIGN KEY (user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment vc_video_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT vc_video_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment video_comment_file_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT video_comment_file_id FOREIGN KEY (file_id) REFERENCES arena_hub.sys_file(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sys_file_caption video_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.sys_file_caption
    ADD CONSTRAINT video_id FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_event video_id_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_event
    ADD CONSTRAINT video_id_fk FOREIGN KEY (video_id) REFERENCES arena_hub.gc_video(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment video_main_comment_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT video_main_comment_id FOREIGN KEY (main_comment_id) REFERENCES arena_hub.gc_video_comment(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment video_reply_comment_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT video_reply_comment_id FOREIGN KEY (reply_comment_id) REFERENCES arena_hub.gc_video_comment(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_video_comment video_target_user_id; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_video_comment
    ADD CONSTRAINT video_target_user_id FOREIGN KEY (target_user_id) REFERENCES arena_hub.gc_user(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: gc_user_video_plays_node vpr_uservideoplay_fk; Type: FK CONSTRAINT; Schema: arena_hub; Owner: postgres
--

ALTER TABLE ONLY arena_hub.gc_user_video_plays_node
    ADD CONSTRAINT vpr_uservideoplay_fk FOREIGN KEY (videoplay_id) REFERENCES arena_hub.gc_user_video_play(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

